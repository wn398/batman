package ${project.packageName}.standard.repository;

import ${project.packageName}.standard.model.${entity.name};
import com.rayleigh.core.customQuery.CustomRepository;
<#if entity.primaryKeyType=="String">
    <#assign entityIdType="String">
<#else>
    <#assign entityIdType="Long">
</#if>
/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/
public interface ${entity.name}Repository extends CustomRepository<${entity.name}, ${entityIdType}> {
}

/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/