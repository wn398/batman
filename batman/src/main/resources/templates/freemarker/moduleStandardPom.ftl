<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>${projectBasePackage}</groupId>
    <artifactId>${projectName}-${moduleName}Standard</artifactId>
    <version>0.0.1-SNAPSHOT</version>
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
        <rayleigh.core.version>0.0.3-SNAPSHOT</rayleigh.core.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.rayleigh</groupId>
            <artifactId>core</artifactId>
            <version>${r"${rayleigh.core.version}"}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${r"${java.version}"}</source>
                    <target>${r"${java.version}"}</target>
                </configuration>
            </plugin>

            <#--<plugin>-->
                <#--<groupId>org.apache.maven.plugins</groupId>-->
                <#--<artifactId>maven-deploy-plugin</artifactId>-->
                <#--<version>2.8.2</version>-->
                <#--<configuration>-->
                    <#--<skip>true</skip>-->
                <#--</configuration>-->
            <#--</plugin>-->
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
    <distributionManagement>
        <repository>
            <id>nexus</id>
            <name>rayleigh Nexus Release Repository</name>
            <url>http://10.0.137.108:8081/repository/maven-releases/</url>
        </repository>
        <snapshotRepository>
            <id>nexus</id>
            <name>rayleigh Nexus Snapshot Repository</name>
            <url>http://10.0.137.108:8081/repository/maven-snapshots/</url>
        </snapshotRepository>
    </distributionManagement>
</project>
