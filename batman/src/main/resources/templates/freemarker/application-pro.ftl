spring.application.name=${module.name}
server.port=${project.port}
server.servlet.context-path=/api
swagger.basePackage=${project.packageName}
batman.encodeDataSource=${project.isEncodeDataSource ?string('true','false')}

spring.main.allow-bean-definition-overriding=true
spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults = false

<#--#发送数据压缩-->
server.compression.enabled=true
server.compression.min-response-size=1024
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain

<#--#jwt 过期时间-->
jwt.expire.millisecond=600000
jwt.secret.key=TBlbena8h4EdhldhIefw+Q==
jwt.noPermission.page.url=/

<#--#是否开启jwt-->
jwt.enabled=false
jwt.exclude.urls=swagger-ui.html,webjars/springfox-swagger-ui,api-docs,/druid/,swagger-resources
<#--
        String port = new Random().nextInt(9000)+8080+"";
        map.put("moduleName","testModel");
        map.put("serverPort",port);
-->
<#--#数据库配置#-->
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.name=main
<#--设置主数据源-->
<#if mainDataSource??>
    <#assign databaseType = GeneratorStringUtil.toLowerCase(mainDataSource.dataBaseType)>
    <#assign hostname = mainDataSource.hostName>
    <#assign port = GeneratorStringUtil.number2String(mainDataSource.port)>
    <#if databaseType == "postgresql">
        <#assign dialect = "org.hibernate.dialect.PostgreSQL9Dialect">
        <#assign driverclassname = "org.postgresql.Driver">
    <#elseif databaseType == "mysql">
        <#assign dialect = "org.hibernate.dialect.MySQL5Dialect">
        <#assign driverclassname = "com.mysql.jdbc.Driver">
    </#if>
spring.datasource.url = jdbc:${databaseType}://${hostname}:${port}/${mainDataSource.dataBaseName}?characterEncoding=UTF-8<#if databaseType == "mysql">&useSSL=false&serverTimezone=GMT%2B8</#if>
spring.datasource.username = ${mainDataSource.username}
spring.datasource.password = ${mainDataSource.password}
spring.datasource.driverClassName =${driverclassname}
spring.jpa.properties.hibernate.dialect=${dialect}
<#--主数据源设置完成-->
<#--设置其它数据源-->
    <#if otherDataSources??>
custom.datasource.names=${otherDataSourceNames}
        <#list otherDataSources as otherDataSource>
            <#assign databaseType = GeneratorStringUtil.toLowerCase(otherDataSource.dataBaseType)>
            <#assign hostname = otherDataSource.hostName>
            <#assign otherDataSourceNickName =otherDataSource.dataSourceNickName >
            <#assign port = GeneratorStringUtil.number2String(otherDataSource.port)>
            <#if databaseType == "postgresql">
                <#assign dialect = "org.hibernate.dialect.PostgreSQL9Dialect">
                <#assign driverclassname = "org.postgresql.Driver">
            <#elseif databaseType == "mysql">
                <#assign dialect = "org.hibernate.dialect.MySQL5Dialect">
                <#assign driverclassname = "com.mysql.jdbc.Driver">
            </#if>
custom.datasource.${otherDataSourceNickName}.url=jdbc:${databaseType}://${hostname}:${port}/${otherDataSource.dataBaseName}?characterEncoding=UTF-8<#if databaseType == "mysql">&useSSL=false&serverTimezone=GMT%2B8</#if>
custom.datasource.${otherDataSourceNickName}.driverClassName =${driverclassname}
custom.datasource.${otherDataSourceNickName}.username=${otherDataSource.username}
custom.datasource.${otherDataSourceNickName}.password=${otherDataSource.password}
        </#list>
    </#if>
<#--其它数据源设置完成-->
<#else>
<#--如果没有主数据源设置h2内存数据库-->
#H2
spring.datasource.url = jdbc:h2:mem:${module.name}
spring.datasource.username = <#if project.isEncodeDataSource==true>gJ+V4EY7dbp8jHpXkG6IyQ==</#if><#if project.isEncodeDataSource==false>root</#if>
spring.datasource.password = <#if project.isEncodeDataSource==true>gJ+V4EY7dbp8jHpXkG6IyQ==</#if><#if project.isEncodeDataSource==false>root</#if>
spring.datasource.driverClassName = org.h2.Driver
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.H2Dialect
</#if>
<#--设为true的话，因为session缓存的作用，会导致在一个请求方法里第二次切换数据源失败，因为缓存不会重新获取链接-->
spring.jpa.open-in-view=false

<#--#日志配置-->
logging.file=../logs/${module.name}.log

# Specify the DBMS
#spring.jpa.database =
# Show or not log for each sql query
spring.jpa.show-sql = false

# Hibernate ddl auto (create, create-drop, update)
spring.jpa.hibernate.ddl-auto = none
spring.jpa.properties.hibernate.format_sql=true

spring.jackson.time-zone=GMT+8

spring.http.encoding.charset=UTF-8

<#--# 下面为连接池的补充设置，应用到上面所有数据源中-->
<#--# 初始化大小，最小，最大-->
spring.datasource.initialSize=5
spring.datasource.minIdle=5
spring.datasource.maxActive=20
<#--# 配置获取连接等待超时的时间-->
spring.datasource.maxWait=60000
<#--# 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒-->
spring.datasource.timeBetweenEvictionRunsMillis=60000
<#--# 配置一个连接在池中最小生存的时间，单位是毫秒-->
spring.datasource.minEvictableIdleTimeMillis=300000
spring.datasource.validationQuery=SELECT 1
spring.datasource.testWhileIdle=true
spring.datasource.testOnBorrow=false
spring.datasource.testOnReturn=false
<#--# 打开PSCache，并且指定每个连接上PSCache的大小-->
spring.datasource.poolPreparedStatements=true
spring.datasource.maxPoolPreparedStatementPerConnectionSize=20
<#--# 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙-->
spring.datasource.filters=stat,slf4j,wall
<#--# 通过connectProperties属性来打开mergeSql功能；慢SQL记录-->
spring.datasource.connectionProperties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
<#--# 合并多个DruidDataSource的监控数据-->
spring.datasource.useGlobalDataSourceStat=true
#druid datasouce database settings end