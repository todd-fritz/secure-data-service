/*
 *
 * Copyright 2012 Shared Learning Collaborative, LLC
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
 *
 */

package org.slc.sli.api.criteriaGenerator;

import com.sun.jersey.spi.container.ContainerRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.NeutralCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 1/15/13
 */

@Component
public class DateFilterCriteriaGenerator {

    @Autowired
    EntityIdentifier entityIdentifier;

    @Autowired
    SessionRangeCalculator sessionRangeCalculator;

    private ThreadLocal<GranularAccessFilter> granularAccessFilterStore = new ThreadLocal<GranularAccessFilter>();
    public void generate(ContainerRequest request) {

        List<String> schoolYears = request.getQueryParameters().get(ParameterConstants.SCHOOL_YEARS);

        // only process if there is a schoolYear query parameter
        if (schoolYears != null && schoolYears.size() > 0) {
            String schoolYearRange = schoolYears.get(0);

            //Extract date range using session
            Pair<String, String> dates = sessionRangeCalculator.findDateRange(schoolYearRange);

            // Find appropriate entity to apply filter
            entityIdentifier.findEntity(request.getPath());
            builder().forEntity(entityIdentifier.getEntityName())
                .withAttributes(entityIdentifier.getBeginDateAttribute(), entityIdentifier.getEndDateAttribute())
                .startingFrom(dates.getLeft())
                .endingTo(dates.getRight())
                .build();
        }
    }
    private DateFilterCriteriaBuilder builder() {
        return new DateFilterCriteriaBuilder();
    }


    private final class DateFilterCriteriaBuilder {
        private String entityName;
        private String beginDate;
        private String endDate;
        private String beginDateAttribute;
        private String endDateAttribute;

        public DateFilterCriteriaBuilder forEntity(String entityName) {
            this.entityName = entityName;
            return this;
        }
        public DateFilterCriteriaBuilder withAttributes(String beginDateAttribute, String endDateAttribute) {
            this.beginDateAttribute = beginDateAttribute;
            this.endDateAttribute = endDateAttribute;
            return this;
        }
        public DateFilterCriteriaBuilder startingFrom(String beginDate) {
            this.beginDate = beginDate;
            return this;
        }
        public DateFilterCriteriaBuilder endingTo(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public void build() {
            GranularAccessFilter granularAccessFilter = new GranularAccessFilter();
            NeutralCriteria neutralCriteria = new NeutralCriteria("");
            granularAccessFilter.setEntityName(entityName);
            granularAccessFilter.setNeutralCriteria(neutralCriteria);
            granularAccessFilterStore.set(granularAccessFilter);
        }

    }
}


