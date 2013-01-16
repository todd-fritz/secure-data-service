/*
 * Copyright 2013 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.criteriaGenerator;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: sashton
 */
@Component
public class SessionRangeCalculator {
    
    private static final String BEGIN_DATE = "beginDate";
    private static final String END_DATE = "endDate";
    private static final String SCHOOL_YEAR = "schoolYear";
    private static final String SCHOOL_ID = "schoolId";
    
    private static final Pattern SCHOOL_YEAR_PATTERN = Pattern.compile("^(\\d{4})-(\\d{4})$");
    
    @Resource
    private EdOrgHelper edOrgHelper;
    
    @Autowired
    protected PagingRepositoryDelegate<Entity> repo;
    
    /**
     * Finds the beginDate and endDate of the sessions which fall with the specified
     * school years (in the format "2005-2012")
     * 
     * @param schoolYearRange
     * @return
     */
    public Pair<String, String> findDateRange(String schoolYearRange) {
        
        Pair<String, String> years = parseDateRange(schoolYearRange);
        
        Set<String> edOrgIds = edOrgHelper.getDirectEdorgs();
        
        Iterable<Entity> sessions = getSessions(years, edOrgIds);
        
        return findMinMaxDates(sessions);
    }
    
    private Pair<String, String> parseDateRange(String schoolYearRange) {
        
        // first, validate the range
        Matcher m = SCHOOL_YEAR_PATTERN.matcher(schoolYearRange);
        
        if (m.find() && m.groupCount() == 2) {
            
            // regex has already verified that these are integers
            String firstYear = (m.group(1));
            String lastYear = (m.group(2));
            
            return new ImmutablePair<String, String>(firstYear, lastYear);
        } else {
            // invalid schoolYearRange
            throw new QueryParseException("Invalid date range", schoolYearRange);
        }
    }
    
    private Iterable<Entity> getSessions(Pair<String, String> years, Set<String> edOrgIds) {
        
        NeutralQuery sessionQuery = new NeutralQuery(new NeutralCriteria(SCHOOL_ID, NeutralCriteria.CRITERIA_IN,
                new ArrayList<String>(edOrgIds)));
        
        sessionQuery.addCriteria(new NeutralCriteria(SCHOOL_YEAR, NeutralCriteria.CRITERIA_GT, years.getLeft()));
        sessionQuery.addCriteria(new NeutralCriteria(SCHOOL_YEAR, NeutralCriteria.CRITERIA_LT, years.getRight()));
        
        Iterable<Entity> sessions = repo.findAll(EntityNames.SESSION, sessionQuery);
        
        return sessions;
    }
    
    private ImmutablePair<String, String> findMinMaxDates(Iterable<Entity> sessions) {
        boolean sessionsLocated = false;
        String earliestDate = "9999";
        String latestDate = "0000";
        
        for (Entity e : sessions) {
            sessionsLocated = true;
            String beginDate = (String) e.getBody().get(BEGIN_DATE);
            String endDate = (String) e.getBody().get(END_DATE);
            
            if (beginDate.compareTo(earliestDate) < 0) {
                earliestDate = beginDate;
            }
            if (endDate.compareTo(latestDate) > 0) {
                latestDate = endDate;
            }
        }
        
        if (!sessionsLocated || "0000".equals(latestDate) || "9999".equals(earliestDate)) {
            // no session dates to filter on
            return new ImmutablePair<String, String>("", "");
        } else {
            return new ImmutablePair<String, String>(earliestDate, latestDate);
        }
    }
    
}
