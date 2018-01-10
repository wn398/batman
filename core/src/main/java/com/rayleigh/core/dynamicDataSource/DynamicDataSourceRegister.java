package com.rayleigh.core.dynamicDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.rayleigh.core.util.AESEncoderUtil;
import com.rayleigh.core.util.MD5Base64Util;
import com.rayleigh.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DynamicDataSourceRegister  implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Logger logger = LoggerFactory.getLogger("DynamicDataSource");
    //如配置文件中未指定数据源类型，使用该默认值
    private static final Object DATASOURCE_TYPE_DEFAULT = "org.apache.tomcat.jdbc.pool.DataSource";
    private ConversionService conversionService = new DefaultConversionService();
    private PropertyValues dataSourcePropertyValues;

    private static Map sourcePoolMap = new HashMap();
    //数据源密钥
    private String sourceRule = MD5Base64Util.getDeBase64("dmFua2VAd2FuZzIw");
    // 默认数据源
    private DataSource defaultDataSource;

    private Map<String, DataSource> customDataSources = new HashMap<String, DataSource>();

    /**
     * 加载多数据源配置
     */
    @Override
    public void setEnvironment(Environment environment) {
        logger.debug("DynamicDataSourceRegister.setEnvironment()");
        initDefaultDataSource(environment);
        initCustomDataSources(environment);
    }

    /**
     * 加载主数据源配置.
     * @param env
     */
    private void initDefaultDataSource(Environment env){
        boolean isEncodeDatasource = false;
        try {
            RelaxedPropertyResolver propertyResolver2 = new RelaxedPropertyResolver(env, "batman.");
            isEncodeDatasource = Boolean.parseBoolean(propertyResolver2.getProperty("encodeDataSource"));
        }catch (Exception e){
            logger.error("解析出错batmam.encodeDataSource出错!");
            e.printStackTrace();
        }
        // 读取主数据源
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
        Map<String, Object> dsMap = new HashMap<String, Object>();
        dsMap.put("type", propertyResolver.getProperty("type"));
        dsMap.put("driverClassName", propertyResolver.getProperty("driverClassName"));
        dsMap.put("url", propertyResolver.getProperty("url"));
        if(isEncodeDatasource){
            dsMap.put("username", AESEncoderUtil.AESDecode(sourceRule, propertyResolver.getProperty("username")));
            dsMap.put("password", AESEncoderUtil.AESDecode(sourceRule, propertyResolver.getProperty("password")));
        }else {
            dsMap.put("username", propertyResolver.getProperty("username"));
            dsMap.put("password", propertyResolver.getProperty("password"));
        }
        sourcePoolMap.put("initialSize",propertyResolver.getProperty("initialSize"));
        sourcePoolMap.put("minIdle",propertyResolver.getProperty("minIdle"));
        sourcePoolMap.put("maxActive",propertyResolver.getProperty("maxActive"));
        sourcePoolMap.put("maxWait",propertyResolver.getProperty("maxWait"));
        sourcePoolMap.put("timeBetweenEvictionRunsMillis",propertyResolver.getProperty("timeBetweenEvictionRunsMillis"));
        sourcePoolMap.put("minEvictableIdleTimeMillis", propertyResolver.getProperty("minEvictableIdleTimeMillis"));
        sourcePoolMap.put("validationQuery", propertyResolver.getProperty("validationQuery"));
        sourcePoolMap.put("testWhileIdle", propertyResolver.getProperty("testWhileIdle"));
        sourcePoolMap.put("testOnBorrow", propertyResolver.getProperty("testOnBorrow"));
        sourcePoolMap.put("testOnReturn",propertyResolver.getProperty("testOnReturn"));
        sourcePoolMap.put("poolPreparedStatements", propertyResolver.getProperty("poolPreparedStatements"));
        sourcePoolMap.put("maxPoolPreparedStatementPerConnectionSize", propertyResolver.getProperty("maxPoolPreparedStatementPerConnectionSize"));
        sourcePoolMap.put("filters",propertyResolver.getProperty("filters"));
        sourcePoolMap.put("connectionProperties",propertyResolver.getProperty("connectionProperties"));
        sourcePoolMap.put("useGlobalDataSourceStat",propertyResolver.getProperty("useGlobalDataSourceStat"));

        dsMap.putAll(sourcePoolMap);
        //创建数据源;
        defaultDataSource = buildDataSource(dsMap);
        dataBinder(defaultDataSource, env);
    }

    /**
     * 初始化更多数据源
     */
    private void initCustomDataSources(Environment env) {
        boolean isEncodeDatasource = false;
        try {
            RelaxedPropertyResolver propertyResolver2 = new RelaxedPropertyResolver(env, "batman.");
            isEncodeDatasource = Boolean.parseBoolean(propertyResolver2.getProperty("encodeDataSource"));
        }catch (Exception e){
            logger.error("解析出错batmam.encodeDataSource出错!");
            e.printStackTrace();
        }
        // 读取配置文件获取更多数据源，也可以通过defaultDataSource读取数据库获取更多数据源
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "custom.datasource.");
        String dsPrefixs = propertyResolver.getProperty("names");
        if(!StringUtil.isEmpty(dsPrefixs)){
            for (String dsPrefix : dsPrefixs.split(",")) {// 多个数据源
                Map<String, Object> dsMap = new HashMap<>();
                //dsMap = propertyResolver.getSubProperties(dsPrefix + ".");
                dsMap.put("driverClassName", propertyResolver.getProperty(dsPrefix+".driverClassName"));
                dsMap.put("url", propertyResolver.getProperty(dsPrefix+".url"));
                if(isEncodeDatasource){
                    dsMap.put("username", AESEncoderUtil.AESDecode(sourceRule, propertyResolver.getProperty(dsPrefix+".username")));
                    dsMap.put("password", AESEncoderUtil.AESDecode(sourceRule, propertyResolver.getProperty(dsPrefix+".password")));
                }else {
                    dsMap.put("username", propertyResolver.getProperty(dsPrefix+".username"));
                    dsMap.put("password", propertyResolver.getProperty(dsPrefix+".password"));
                }
                dsMap.putAll(sourcePoolMap);
                DataSource ds = buildDataSource(dsMap);
                customDataSources.put(dsPrefix, ds);
                dataBinder(ds, env);
            }
        }
    }


    public DataSource buildDataSource(Map<String, Object> dsMap) {
        Object type = dsMap.get("type");
        if (type == null){
            type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource
        }
        Class<? extends DataSource> dataSourceType;

        try {
            dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
            String driverClassName = dsMap.get("driverClassName").toString();
            String url = dsMap.get("url").toString();
            String username = dsMap.get("username").toString();
            String password = dsMap.get("password").toString();
            DruidDataSource dataSource = new DruidDataSource();

            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setDriverClassName(driverClassName);

            int initialSize = Integer.parseInt((String)dsMap.get("initialSize"));
            int minIdle = Integer.parseInt((String)dsMap.get("minIdle"));
            int maxActive = Integer.parseInt((String) dsMap.get("maxActive"));
            int maxWait = Integer.parseInt((String) dsMap.get("maxWait"));
            int timeBetweenEvictionRunsMillis = Integer.parseInt((String)dsMap.get("timeBetweenEvictionRunsMillis"));
            int minEvictableIdleTimeMillis = Integer.parseInt((String) dsMap.get("minEvictableIdleTimeMillis"));
            String validationQuery = (String)dsMap.get("validationQuery");
            boolean testWhileIdle = Boolean.parseBoolean((String)dsMap.get("testWhileIdle"));
            boolean testOnBorrow = Boolean.parseBoolean((String) dsMap.get("testOnBorrow"));
            boolean testOnReturn = Boolean.parseBoolean((String) dsMap.get("testOnReturn"));
            boolean poolPreparedStatements = Boolean.parseBoolean((String) dsMap.get("poolPreparedStatements"));
            int maxPoolPreparedStatementPerConnectionSize = Integer.parseInt((String) dsMap.get("maxPoolPreparedStatementPerConnectionSize"));
            String filters = (String)dsMap.get("filters");
            String connectionProperties = (String)dsMap.get("connectionProperties");
            boolean useGlobalDataSourceStat = Boolean.parseBoolean((String) dsMap.get("useGlobalDataSourceStat"));

            dataSource.setInitialSize(initialSize);
            dataSource.setMinIdle(minIdle);
            dataSource.setMaxActive(maxActive);
            dataSource.setMaxWait(maxWait);
            dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
            dataSource.setValidationQuery(validationQuery);
            dataSource.setTestWhileIdle(testWhileIdle);
            dataSource.setTestOnBorrow(testOnBorrow);
            dataSource.setTestOnReturn(testOnReturn);
            dataSource.setPoolPreparedStatements(poolPreparedStatements);
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
            dataSource.setFilters(filters);
            Properties properties = new Properties();
            for(String item : connectionProperties.split(";")){
                String[] attr = item.split("=");
                properties.put(attr[0].trim(), attr[1].trim());
            }
            dataSource.setConnectProperties(properties);
            dataSource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
            return dataSource;
//            DataSourceBuilder factory =   DataSourceBuilder.create().driverClassName(driverClassName).url(url).username(username).password(password).type(dataSourceType);
//            return factory.build();
        } catch (RuntimeException e) {
            logger.error("初始化数据源出错!");
            e.printStackTrace();
        }catch (Exception e){
            logger.error("初始化数据源出错!");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 为DataSource绑定更多数据
     * @param dataSource
     * @param env
     */
    private void dataBinder(DataSource dataSource, Environment env){
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
        dataBinder.setConversionService(conversionService);
        dataBinder.setIgnoreNestedProperties(false);//false
        dataBinder.setIgnoreInvalidFields(false);//false
        dataBinder.setIgnoreUnknownFields(true);//true

        if(dataSourcePropertyValues == null){
            Map<String, Object> rpr = new RelaxedPropertyResolver(env, "spring.datasource").getSubProperties(".");
            Map<String, Object> values = new HashMap<>(rpr);
            // 排除已经设置的属性
            values.remove("type");
            values.remove("driverClassName");
            values.remove("url");
            values.remove("username");
            values.remove("password");
            dataSourcePropertyValues = new MutablePropertyValues(values);
        }
        dataBinder.bind(dataSourcePropertyValues);

    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        logger.debug("DynamicDataSourceRegister.registerBeanDefinitions()");
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        // 将主数据源添加到更多数据源中
        targetDataSources.put("dataSource", defaultDataSource);
        DynamicDataSourceContextHolder.getDataSourceIds().add("dataSource");
        // 添加更多数据源
        targetDataSources.putAll(customDataSources);
        for (String key : customDataSources.keySet()) {
            DynamicDataSourceContextHolder.getDataSourceIds().add(key);
        }

        // 创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);

        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        //添加属性：AbstractRoutingDataSource.defaultTargetDataSource
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetDataSources);
        registry.registerBeanDefinition("dataSource", beanDefinition);
    }

}
 
