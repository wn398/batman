<?xml version="1.0" encoding="UTF-8"?>

<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <property resource="application.properties"/>
    <!--定义的值会被插入到logger上下文中。定义变量后，可以使“${r'${}'}”来使用变量-->
    <property name="test" value="test"/>
    <!--每个logger都关联到logger上下文，默认上下文名称为“default”。但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改,可以通过%contextName来打印日志上下文名称-->
    <contextName>${module.name}</contextName>
    <!--输出到控制台-->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <!--ThresholdFilter为系统定义的拦截器-->
        <!--<filter class="ch.qos.logback.classic.filter.ThresholdFilter">-->
        <!--<level>ERROR</level>-->
        <!--</filter>-->
        <!--表示对日志进行编码-->
        <encoder>
            <pattern>%X{traceId} %X{ip} %X{method} %X{path} --- %d [%t] %-5p [%c] %m%n</pattern>
        </encoder>
    </appender>

    <!--输出到文件-->
    <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>${r'${logging.file}'}</File>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <FileNamePattern>${r'${logging.file}'}_%d{yyyy-MM-dd}.log</FileNamePattern>
            <!--最多保留日志个数-->
            <maxHistory>31</maxHistory>
            <!--到了这个大小后就会删掉旧的日志-->
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>%X{traceId} %X{ip} %X{method} %X{path} --- %d [%t] %-5p [%c] %m%n</Pattern>
        </layout>
        <encoder>
            <pattern>%X{traceId} %X{ip} %X{method} %X{path} --- %d [%t] %-5p [%c] %m%n</pattern>
        </encoder>
    </appender>
    <!--logger单独文件示例-->
    <!--<appender name="testFile" class="ch.qos.logback.core.rolling.RollingFileAppender">-->
    <!--<File>../logs/testController.log</File>-->
    <!--<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">-->
    <!--<FileNamePattern>test_%d{yyyy-MM-dd}.log</FileNamePattern>-->
    <!--</rollingPolicy>-->
    <!--<triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">-->
    <!--<MaxFileSize>200MB</MaxFileSize>-->
    <!--</triggeringPolicy>-->
    <!--<encoder>-->
    <!--<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %contextName [%t] %-5p [%c{36}] - %m%n</pattern>-->
    <!--</encoder>-->
    <!--</appender>-->
    <!--&lt;!&ndash;addActivity:是否向上级logger传递打印信息。默认是true&ndash;&gt;-->
    <!--<logger name="com.tim.extend.controller.TestExtendController" level="info" addActivity="false">-->
    <!--<appender-ref ref="testFile"/>-->
    <!--</logger>-->
    <logger name="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping" level="ERROR"/>
    <logger name="springfox" level="ERROR"/>
    <!--日志级别有：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF-->
    <root level="INFO">
        <appender-ref ref="console"/>
        <appender-ref ref="file"/>
    </root>
</configuration>
