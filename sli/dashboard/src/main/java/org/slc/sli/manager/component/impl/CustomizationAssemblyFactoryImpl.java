package org.slc.sli.manager.component.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.Config.Item;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.Manager;
import org.slc.sli.manager.Manager.EntityMappingManager;
import org.slc.sli.manager.UserEdOrgManager;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.util.DashboardException;
import org.slc.sli.util.ExecutionTimeLogger.LogExecutionTime;
import org.slc.sli.util.SecurityUtil;

/**
 * Implementation of the CustomizationAssemblyFactory
 * @author agrebneva
 *
 */
public class CustomizationAssemblyFactoryImpl implements CustomizationAssemblyFactory, ApplicationContextAware {
    public static final Class<?>[] ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE =
            new Class[]{String.class, Object.class, Config.Data.class};
    public static final String SUBSTITUTE_TOKEN_PATTERN = "\\$\\{([^}]+)\\}";
    private static final String DATA_CACHE_REGION = "user.panel.data";
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationContext applicationContext;
    private ConfigManager configManager;
    private UserEdOrgManager userEdOrgManager;
    private Map<String, InvokableSet> entityReferenceToManagerMethodMap;
    private CacheManager cacheManager;
    private boolean useCache;


    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void setUserEdOrgManager(UserEdOrgManager userEdOrgManager) {
        this.userEdOrgManager = userEdOrgManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    protected String getTokenId() {
        return SecurityUtil.getToken();
    }

    protected Config getConfig(String componentId) {
        return configManager.getComponentConfig(userEdOrgManager.getUserEdOrg(getTokenId()), componentId);
    }

    @Override
    public Collection<Config> getWidgetConfigs() {
        return configManager.getWidgetConfigs(userEdOrgManager.getUserEdOrg(getTokenId()));
    }

    /**
     * Check declared condition against the entity
     * @param config - config for the component
     * @param entity - entity for the component
     * @return true if condition passes and false otherwise
     */
    @SuppressWarnings("unchecked")
    public final boolean checkCondition(Config parentConfig, Config config, GenericEntity entity) {
        if (config != null && config.getCondition() != null) {
            //todo: figure out what to do when no entity
            if (entity == null) {
                return true;
            }
            Config.Condition condition = config.getCondition();
            Object[] values = condition.getValue();
            // for simplicity always treat as an array
            List<GenericEntity> listOfEntitites = (parentConfig != null && parentConfig.getRoot() != null) ? entity.getList(parentConfig.getRoot()) : Arrays.asList(entity);
            Object childEntity;
            // condition is equivalent to exists in the list
            for (GenericEntity oneEntity : listOfEntitites) {
                childEntity = getValue(oneEntity, condition.getField());
                // if doesn't exist, assume true - this is not entitlements, so it should not be restrictive
                if (childEntity == null) {
                    return true;
                }
                if (childEntity instanceof Number) {
                    double childNumber = ((Number) childEntity).doubleValue();
                    for (Object n : values) {
                        if (childNumber == ((Number) n).doubleValue()) {
                            return true;
                        }
                    }
                } else if (childEntity instanceof String) {
                    String childString = (String) childEntity;
                    for (Object n : values) {
                        if (childString.equalsIgnoreCase((String) n)) {
                            return true;
                        }
                    }
                } else {
                    throw new DashboardException("Unsupported data type for condition. Only allow string and numbers");
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Get value from the entity model map where sub-entities are identified by a dot
     * "data.history.id"
     * @param entity
     * @param dataField
     * @return
     */
    private Object getValue(GenericEntity entity, String dataField) {
        String[] pathTokens = dataField.split("\\.");
        pathTokens = (pathTokens.length == 0) ? new String[]{dataField} : pathTokens;
        Object childEntity = entity;
        for (String token : pathTokens) {
            if (childEntity == null || !(childEntity instanceof GenericEntity)) {
                return null;
            }
            childEntity = ((GenericEntity) childEntity).get(token);
        }
        return childEntity;
    }

    /**
     * Traverse the config tree and populate the necessary entity and config objects
     * @param model - model to populate
     * @param componentId - current component to explore
     * @param entityKey - entityKey
     * @param parentEntity - parent entity
     * @param depth - depth of the recursion
     */
    private Config populateModelRecursively(
        ModelAndViewConfig model, String componentId, Object entityKey, Config.Item parentToComponentConfigRef, Config panelConfig,
        GenericEntity parentEntity, int depth, boolean lazyOverride
    ) {
        if (depth > 5) {
            throw new DashboardException("The items hierarchy is too deep - only allow 5 elements");
        }
        Config config = parentToComponentConfigRef;
        GenericEntity entity = parentEntity;
        if (parentToComponentConfigRef == null || parentToComponentConfigRef.getType().hasOwnConfig()) {
            config = getConfig(componentId);
            if (config == null) {
                throw new DashboardException(
                        "Unable to find config for " + componentId + " and entity id " + entityKey + ", config " + componentId);
            }
            Config.Data dataConfig = config.getData();
            if (dataConfig != null && (!dataConfig.isLazy() || lazyOverride) && !model.hasDataForAlias(dataConfig.getCacheKey())) {
                entity = getDataComponent(componentId, entityKey, dataConfig);
                model.addData(dataConfig.getCacheKey(), entity);
            }
            if (!checkCondition(config, config, entity)) {
                return null;
            }
        }
        if (config.getItems() != null) {
            List<Config.Item> items = new ArrayList<Config.Item>();
            depth++;
            Config newConfig;
            // get items, go through all of them and update config as need according to conditions and template substitutions
            for (Config.Item item : getUpdatedDynamicHeaderTemplate(config, entity)) {
                if (checkCondition(config, item, entity)) {
                    newConfig = populateModelRecursively(model, item.getId(), entityKey, item, config, entity, depth, lazyOverride);
                    if (newConfig != null) {
                        item = (Item) item.cloneWithItems(newConfig.getItems());
                    }
                    items.add(item);
                    if (config.getType().isLayoutItem()) {
                        model.addLayoutItem(newConfig);
                    }
                }
            }
            config = config.cloneWithItems(items.toArray(new Config.Item[0]));
        }
        if (componentId != null) {
            model.addComponentViewConfigMap(componentId, config);
        }
        return config;
    }

    /**
     * Replace tokens in headers with values from entity's internal metadata
     * @param config
     * @param entity
     */
    protected Config.Item[] getUpdatedDynamicHeaderTemplate(Config config, GenericEntity entity) {
        if (entity != null) {
            Pattern p = Pattern.compile(SUBSTITUTE_TOKEN_PATTERN);
            Matcher matcher;
            String name, value;
            Collection<Config.Item> newItems = new ArrayList<Config.Item>();
            for (Config.Item item : config.getItems()) {
                name = item.getName();
                if (name != null) {
                    matcher = p.matcher(name);
                    while (matcher.find()) {
                        value = (String) getValue(entity, matcher.group(1));
                        if (value != null) {
                            name = name.replace(matcher.group(), value);
                        }
                    }
                    item = item.cloneWithName(name);
                }
                newItems.add(item);
            }
            return newItems.toArray(new Config.Item[0]);
        }
        return config.getItems();
    }

    @Override
    public ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey) {
        return getModelAndViewConfig(componentId, entityKey, false);
    }

    @Override
    @LogExecutionTime
    public ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey, boolean lazyOverride) {

        ModelAndViewConfig modelAndViewConfig = new ModelAndViewConfig();
        populateModelRecursively(modelAndViewConfig, componentId, entityKey, null, null, null, 0, lazyOverride);
        return modelAndViewConfig;
    }


    /**
     * Internal convenience class for method caching
     * @author agrebneva
     *
     */
    private class InvokableSet {
        Object manager;
        Method method;
        InvokableSet(Object manager, Method method) {
            this.manager = manager;
            this.method = method;
        }
        public Object getManager() {
            return manager;
        }
        public Method getMethod() {
            return method;
        }
    }

    private void populateEntityReferenceToManagerMethodMap() {
        Map<String, InvokableSet> entityReferenceToManagerMethodMap = new HashMap<String, InvokableSet>();

        boolean foundInterface = false;
        for (Object manager : applicationContext.getBeansWithAnnotation(EntityMappingManager.class).values()) {
            logger.info(manager.getClass().getCanonicalName());
            // managers can be advised (proxied) so original annotation are not seen on the method but
            // still available on the interface
            foundInterface = false;
            for (Class<?> type : manager.getClass().getInterfaces()) {
                if (type.isAnnotationPresent(EntityMappingManager.class)) {
                    foundInterface = true;
                    findEntityReferencesForType(entityReferenceToManagerMethodMap, type, manager);
                }
            }
            if (!foundInterface) {
                findEntityReferencesForType(entityReferenceToManagerMethodMap, manager.getClass(), manager);
            }
        }
        this.entityReferenceToManagerMethodMap = Collections.unmodifiableMap(entityReferenceToManagerMethodMap);
    }

    private final void findEntityReferencesForType(
            Map<String, InvokableSet> entityReferenceToManagerMethodMap, Class<?> type, Object instance) {
        Manager.EntityMapping entityMapping;
        for (Method m : type.getDeclaredMethods()) {
            entityMapping = m.getAnnotation(Manager.EntityMapping.class);
            if (entityMapping != null) {
                if (entityReferenceToManagerMethodMap.containsKey(entityMapping.value())) {
                    throw new DashboardException("Duplicate entity mapping references found for "
                            + entityMapping.value() + ". Fix!!!");
                }
                if (!Arrays.equals(ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE, m.getParameterTypes())) {
                    throw new DashboardException("Wrong signature for the method for "
                            + entityMapping.value() + ". Expected is "
                            + Arrays.asList(ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE) + "!!!");
                }
                entityReferenceToManagerMethodMap.put(entityMapping.value(), new InvokableSet(instance, m));
            }
        }
    }

    protected InvokableSet getInvokableSet(String entityRef) {
        return this.entityReferenceToManagerMethodMap.get(entityRef);
    }

    /**
     * For UTs
     * @param entityRef
     * @return
     */
    public boolean hasCachedEntityMapperReference(String entityRef) {
        return this.entityReferenceToManagerMethodMap.containsKey(entityRef);
    }

    @Override
    public GenericEntity getDataComponent(String componentId, Object entityKey) {
        return getDataComponent(componentId, entityKey, getConfig(componentId).getData());
    }

    protected GenericEntity getCached(CacheKey cacheKey) {
        if (cacheManager != null && this.useCache) {
            Element elem = cacheManager.getCache(DATA_CACHE_REGION).get(cacheKey);
            if (elem != null) {
                return (GenericEntity) elem.getValue();
            }
        }
        return null;
    }

    protected void addCached(CacheKey cacheKey, GenericEntity value) {
        if (cacheManager != null && this.useCache) {
            cacheManager.getCache(DATA_CACHE_REGION).put(new Element(cacheKey, value));
        }
    }

    @LogExecutionTime
    protected GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
        CacheKey cacheKey = new CacheKey(getTokenId(), componentId, entityKey, config);
        GenericEntity value = getCached(cacheKey);
        if (value != null) {
            return value;
        }
        InvokableSet set = this.getInvokableSet(config.getEntityRef());
        if (set == null) {
            throw new DashboardException("No entity mapping references found for " + config.getEntityRef() + ". Fix!!!");
        }
        try {
            value = (GenericEntity) set.getMethod().invoke(set.getManager(), getTokenId(), entityKey, config);
            addCached(cacheKey, value);
            return value;
        } catch (Throwable t) {
            logger.error("Unable to invoke population manager for " + componentId + " and entity id " + entityKey
                    + ", config " + componentId, t);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
       populateEntityReferenceToManagerMethodMap();
    }

    /**
     * Internal generic key type for panel.data
     * @author agrebneva
     *
     */
    private static final class CacheKey {
        private String componentId;
        private Object entityKey;
        private Config.Data config;
        private String tokenId;

        private CacheKey(String token, String componentId, Object entityKey, Config.Data config) {
            this.componentId = componentId;
            this.entityKey = entityKey;
            this.config = config;
            this.tokenId = token;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = prime + ((componentId == null) ? 0 : componentId.hashCode());
            result = prime * result + ((config == null) ? 0 : config.hashCode());
            result = prime * result + ((entityKey == null) ? 0 : entityKey.hashCode());
            result = prime * result + ((tokenId == null) ? 0 : tokenId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            if (componentId == null) {
                if (other.componentId != null) {
                    return false;
                }
            } else if (!componentId.equals(other.componentId)) {
                return false;
            }
            if (config == null) {
                if (other.config != null) {
                    return false;
                }
            } else if (!config.equals(other.config)) {
                return false;
            }
            if (entityKey == null) {
                if (other.entityKey != null) {
                    return false;
                }
            } else if (!entityKey.equals(other.entityKey)) {
                return false;
            }
            if (tokenId == null) {
                if (other.tokenId != null) {
                    return false;
                }
            } else if (!tokenId.equals(other.tokenId)) {
                return false;
            }
            return true;
        }
    }
}
