spring.application.name=${module.name}
server.port=${project.port}
server.context-path=/api
swagger.basePackage=${project.packageName}

#jwt 过期时间
jwt.expire.millisecond=600000
#是否开启jwt
jwt.enabled=false
jwt.exclude.urls=swagger-ui.html,webjars/springfox-swagger-ui,api-docs,/druid/,swagger-resources
<#--
        String port = new Random().nextInt(9000)+8080+"";
        map.put("moduleName","testModel");
        map.put("serverPort",port);
-->
#数据库配置#
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.name=main
spring.datasource.url = jdbc:postgresql://localhost:5432/${module.name}
spring.datasource.username = postgres
spring.datasource.password = 541998
spring.datasource.driverClassName =org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL9Dialect
spring.jpa.open-in-view=true


# Specify the DBMS
#spring.jpa.database =
# Show or not log for each sql query
spring.jpa.show-sql = true

# Hibernate ddl auto (create, create-drop, update)
spring.jpa.hibernate.ddl-auto = update
spring.jpa.properties.hibernate.format_sql=true

spring.jackson.time-zone=GMT+8

spring.http.encoding.charset=UTF-8

# 下面为连接池的补充设置，应用到上面所有数据源中
# 初始化大小，最小，最大
spring.datasource.initialSize=5
spring.datasource.minIdle=5
spring.datasource.maxActive=20
# 配置获取连接等待超时的时间
spring.datasource.maxWait=60000
# 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
spring.datasource.timeBetweenEvictionRunsMillis=60000
# 配置一个连接在池中最小生存的时间，单位是毫秒
spring.datasource.minEvictableIdleTimeMillis=300000
spring.datasource.validationQuery=SELECT 1 FROM DUAL
spring.datasource.testWhileIdle=true
spring.datasource.testOnBorrow=false
spring.datasource.testOnReturn=false
# 打开PSCache，并且指定每个连接上PSCache的大小
spring.datasource.poolPreparedStatements=true
spring.datasource.maxPoolPreparedStatementPerConnectionSize=20
# 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
spring.datasource.filters=stat,wall,log4j
# 通过connectProperties属性来打开mergeSql功能；慢SQL记录
spring.datasource.connectionProperties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
# 合并多个DruidDataSource的监控数据
spring.datasource.useGlobalDataSourceStat=true
#druid datasouce database settings end