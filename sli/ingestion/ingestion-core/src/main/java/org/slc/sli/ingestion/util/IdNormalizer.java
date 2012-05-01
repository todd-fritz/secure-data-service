package org.slc.sli.ingestion.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.text.WordUtils;
import org.mortbay.log.Log;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * 
 * @author ablum
 * 
 */
public class IdNormalizer {
    
    private static final String METADATA_BLOCK = "metaData";
    
    /**
     * Resolve references defined by external IDs (from clients) with internal IDs from SLI data
     * store. Uses a multiple search criteria to resolve the reference
     * 
     * @param collection
     *            Referenced collection
     * @param tenantId
     *            ID namespace that uniquely identifies external ID
     * @param externalSearchCriteria
     *            Search criteria that is used to resolve an externalId
     * @param errorReport
     *            Error reporting
     * @return The resolved internalId
     */
    public static String resolveInternalId(Repository<Entity> entityRepository, String collection, String tenantId, Map<?, ?> externalSearchCriteria, ErrorReport errorReport) {
        Map<String, String> filterFields = new HashMap<String, String>();
        filterFields.put(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey(), tenantId);
        
        NeutralQuery query = new NeutralQuery();
        resolveSearchCriteria(entityRepository, collection, filterFields, externalSearchCriteria, query, tenantId, errorReport);
        
        Entity e = entityRepository.findOne(collection, query);
        Log.debug("~Entity~ {}", e == null ? "Not Found" : e);
        if (e == null) {
            errorReport.error("Cannot find [" + collection + "] record using the following filter: " + query, IdNormalizer.class);
            
            return null;
        }
        
        return e.getEntityId();
    }
    
    /**
     * Resolve references defined by external IDs (from clients) with internal IDs from SLI data
     * store.
     * 
     * @param collection
     *            Referenced collection
     * @param tenantId
     *            ID namespace that uniquely identifies external ID
     * @param externalId
     *            External ID to be resolved
     * @param errorReport
     *            Error reporting
     * @return Resolved internal ID
     */
    public static String resolveInternalId(Repository<Entity> entityRepository, String collection, String tenantId, String externalId, ErrorReport errorReport) {
        NeutralQuery nq = new NeutralQuery();
        nq.addCriteria(new NeutralCriteria(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey(), "=", tenantId, false));
        nq.addCriteria(new NeutralCriteria(METADATA_BLOCK + "." + EntityMetadataKey.EXTERNAL_ID.getKey(), "=", externalId, false));
        nq.setIncludeFields("_id");
        
        Entity e = entityRepository.findOne(collection, nq);
        Log.debug("~Entity~ {}", e == null ? "Not Found" : e);
        if (e == null) {
            errorReport.error("Cannot find [" + collection + "] record using the following filter: " + nq, IdNormalizer.class);
            
            return null;
        }
        
        return e.getEntityId();
    }
    
    /**
     * Adds the criteria that searches within the same collection to the query
     * 
     * @param collection
     * @param filterFields
     * @param externalSearchCriteria
     * @param query
     */
    private static void resolveSearchCriteria(Repository<Entity> entityRepository, String collection, Map<String, String> filterFields, Map<?, ?> externalSearchCriteria, NeutralQuery query, String tenantId, ErrorReport errorReport) {
        for (Map.Entry<?, ?> searchCriteriaEntry : externalSearchCriteria.entrySet()) {
            
            StringTokenizer tokenizer = new StringTokenizer(searchCriteriaEntry.getKey().toString(), "#");
            String pathCollection = tokenizer.nextToken();
            pathCollection = WordUtils.uncapitalize(pathCollection);
            
            if (pathCollection.equals(collection) && searchCriteriaEntry.getValue() != null) {
                
                resolveSameCollectionCriteria(filterFields, searchCriteriaEntry.getKey().toString(), searchCriteriaEntry.getValue());
                addSearchPathsToQuery(query, filterFields);
                
            } else {
                
                resolveDifferentCollectionCriteria(entityRepository, query, searchCriteriaEntry, tenantId, errorReport);
                
            }
        }
        
    }
    
    /**
     * Recursively traverses a complex reference and adds all mongo path and value pairs to the
     * query filter
     * 
     * @param filterFields
     *            Mongo path and value pairs that are filtered in the query
     * @param key
     * @param value
     */
    private static void resolveSameCollectionCriteria(Map<String, String> filterFields, String key, Object value) {
        if (String.class.isInstance(value)) {
            
            StringTokenizer tokenizer = new StringTokenizer(key, "#");
            tokenizer.nextToken();
            String newPath = tokenizer.nextToken();
            filterFields.put(newPath, value.toString());
            
        } else if (Map.class.isInstance(value)) {
            
            for (Map.Entry<?, ?> searchCriteriaEntry : ((Map<?, ?>) value).entrySet()) {
                
                resolveSameCollectionCriteria(filterFields, searchCriteriaEntry.getKey().toString(), searchCriteriaEntry.getValue());
                
            }
            
        } else if (List.class.isInstance(value)) {
            
            for (Object object : (List<?>) value) {
                
                resolveSameCollectionCriteria(filterFields, key, object);
                
            }
            
        }
        
    }
    
    /**
     * Adds the criteria that searches within a different collection to the query
     * 
     * @param query
     * @param externalSearchCriteria
     * @param errorReport
     */
    private static void resolveDifferentCollectionCriteria(Repository<Entity> entityRepository, NeutralQuery query, Map.Entry<?, ?> searchCriteriaEntry, String tenantId, ErrorReport errorReport) {
        StringTokenizer tokenizer = new StringTokenizer(searchCriteriaEntry.getKey().toString(), "#");
        String pathCollection = tokenizer.nextToken();
        pathCollection = WordUtils.uncapitalize(pathCollection);
        String referencePath = tokenizer.nextToken();
        
        Map<String, String> tempFilter = new HashMap<String, String>();
        NeutralQuery referenceQuery = new NeutralQuery();
        if (searchCriteriaEntry.getValue() instanceof String) {
            Map<String, String> searchCriteriaEntryMap = new HashMap<String, String>();
            searchCriteriaEntryMap.put((String) searchCriteriaEntry.getKey(), (String) searchCriteriaEntry.getValue());
            resolveSearchCriteria(entityRepository, pathCollection, tempFilter, searchCriteriaEntryMap, referenceQuery, tenantId, errorReport);
        } else {
            resolveSearchCriteria(entityRepository, pathCollection, tempFilter, (Map<?, ?>) searchCriteriaEntry.getValue(), referenceQuery, tenantId, errorReport);
        }
        
        if (tempFilter.isEmpty()) {
            
            return;
        }
        tempFilter.put(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey(), tenantId);
        Iterable<Entity> referenceFound = entityRepository.findByPaths(pathCollection, tempFilter);
        
        if (referenceFound == null || !referenceFound.iterator().hasNext()) {
            errorReport.error("Cannot find [" + pathCollection + "] record using the following filter: " + tempFilter.toString(), IdNormalizer.class);
        } else {
            
            Map<String, String> orFilter = new HashMap<String, String>();
            
            for (Entity found : referenceFound) {
                
                orFilter.put(referencePath, found.getEntityId());
                
            }
            
            addOrToQuery(query, orFilter);
        }
        
    }
    
    private static NeutralQuery addSearchPathsToQuery(NeutralQuery query, Map<String, String> searchPaths) {
        for (Map.Entry<String, String> field : searchPaths.entrySet()) {
            NeutralCriteria criteria = new NeutralCriteria(field.getKey(), "=", field.getValue());
            query.addCriteria(criteria);
        }
        
        return query;
    }
    
    private static void addOrToQuery(NeutralQuery query, Map<String, String> orFilter) {
        for (Map.Entry<String, String> field : orFilter.entrySet()) {
            query.addOrQuery(new NeutralQuery(new NeutralCriteria(field.getKey(), "=", field.getValue())));
        }
    }
}
