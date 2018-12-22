# ---> Java
*.class

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files #
*.jar
*.war
*.ear

# virtual machine crash logs, see http://www.java.com/en/download/help/error_hotspot.xml
hs_err_pid*
*.iml
.idea
logging.**
<#list project.modules as module>
${module.name}/src/main/java/${project.packageName ?replace(".","/")}/base/**
</#list>

<#list project.modules as module>
${module.name}/target/**
</#list>