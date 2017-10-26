spring.application.name=${module.name}
server.port=${project.port}
server.context-path=/api
swagger.basePackage=${project.packageName}

#发送数据压缩
server.compression.enabled=true
server.compression.min-response-size=1024
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain

#jwt 过期时间
jwt.expire.millisecond=600000
jwt.secret.key=TBlbena8h4EdhldhIefw+Q==
jwt.noPermission.page.url=/

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
#Pgsql
spring.datasource.url = jdbc:postgresql://localhost:5432/${module.name}
spring.datasource.username = postgres
spring.datasource.password = 541998
spring.datasource.driverClassName =org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL9Dialect

#其它数据源名字 ds1,ds2这样区分开配置，多数据源开启@EnableDynamicDataSource,在需要切换的方法上注解 @TargetDataSource("ds1")
#custom.datasource.names=ds1
#custom.datasource.ds1.driverClassName =org.postgresql.Driver
#custom.datasource.ds1.url=jdbc:postgresql://localhost:5432/cources
#custom.datasource.ds1.username=postgres
#custom.datasource.ds1.password=541998

#MYSQL
#spring.jpa.database=mysql
#spring.datasource.url = jdbc:mysql://localhost:3306/${module.name}?characterEncoding=UTF-8
#spring.datasource.username=root
#spring.datasource.password=root
#spring.datasource.driverClassName=com.mysql.jdbc.Driver
#spring.jpa.hibernate.naming-strategy = org.hibernate.cfg.ImprovedNamingStrategy
#spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect

spring.jpa.open-in-view=true

#日志配置
logging.file=../logs/${module.name}.log

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
spring.datasource.filters=stat,wall,slf4j
# 通过connectProperties属性来打开mergeSql功能；慢SQL记录
spring.datasource.connectionProperties=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
# 合并多个DruidDataSource的监控数据
spring.datasource.useGlobalDataSourceStat=true
#druid datasouce database settings end