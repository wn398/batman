<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.rayleigh.${projectName}</groupId>
    <artifactId>${moduleName}</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <#--<version>1.5.2.RELEASE</version>-->
        <version>2.1.1.RELEASE</version>

    </parent>

    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <junit.version>4.12</junit.version>
        <rayleigh.core.version>0.0.3-SNAPSHOT</rayleigh.core.version>
        <start-class>${basePackage}.${moduleName ?cap_first}Application</start-class>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.rayleigh</groupId>
            <artifactId>core</artifactId>
            <version>${r"${rayleigh.core.version}"}</version>
        </dependency>

        <dependency>
            <groupId>${basePackage}</groupId>
            <artifactId>${projectName}-${moduleName}Base</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${r"${junit.version}"}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <#if isMemDatasource==true>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <!--<scope>runtime</scope>-->
        </dependency>
        </#if>
        <!--postgresql-->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!--mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-undertow</artifactId>
        </dependency>
        <!--devtools热部署-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional> <!-- optional=true,依赖不会传递，该项目依赖devtools；之后依赖myboot项目的项目如果想要使用devtools，需要重新引入  -->
        </dependency>

        <dependency>
            <groupId>com.googlecode.log4jdbc</groupId>
            <artifactId>log4jdbc</artifactId>
            <version>1.2</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.codehaus.mojo</groupId>
                    <artifactId>findbugs-maven-plugin</artifactId>
                    <version>3.0.4</version>
                </plugin>
            </plugins>
        </pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${r"${java.version}"}</source>
                    <target>${r"${java.version}"}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>findbugs-maven-plugin</artifactId>
                <configuration>
                    <effort>default</effort>
                    <failOnError>true</failOnError>
                    <includeFilterFile>findbugs-include.xml</includeFilterFile>
                    <!--
                    <excludeFilterFile>findbugs-exclude.xml</excludeFilterFile>
                    <includeFilterFile>findbugs-include.xml</includeFilterFile>-->
                    <!--跳过的类型-->
                    <omitVisitors>FindDeadLocalStores,UnreadFields</omitVisitors>
                    <!--第三方jar检测-->
                    <!--<pluginList>myDetectors.jar, yourDetectors.jar</pluginList>-->

                </configuration>
                <executions>
                    <execution>
                        <id>check</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>deploy</phase>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                        <configuration>
                            <finalName>${r"${project.artifactId}"}-boot</finalName>
                        </configuration>
                    </execution>
                </executions>
                <configuration>
                    <fork>true</fork>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <version>2.8.2</version>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <repositories>
        <repository>
            <id>maven-central</id>
            <name>maven-central</name>
            <url>http://10.0.137.108:8081/repository/maven-central/</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
            <releases>
                <enabled>true</enabled>
            </releases>
        </repository>
        <repository>
            <id>nexus</id>
            <name>rayleigh maven snapshots</name>
            <url>http://10.0.137.108:8081/repository/maven-snapshots/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
                <checksumPolicy>warn</checksumPolicy>
            </snapshots>
        </repository>
    </repositories>

</project>
<#--
        map.put("projectName","testProject");
        map.put("moduleName","testModel");
        map.put("basePackage","com.test");
        map.put("springBootVersion","1.5.4.RELEASE");
-->