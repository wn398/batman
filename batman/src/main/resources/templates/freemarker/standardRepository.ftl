<#include "CopyRight.ftl">
package ${project.packageName}.base.repository;

import ${project.packageName}.base.model.${entity.name};
import com.rayleigh.core.customQuery.CustomRepository;
<#if entity.primaryKeyType=="String">
    <#assign entityIdType="String">
<#else>
    <#assign entityIdType="Long">
</#if>

public interface ${entity.name}Repository extends CustomRepository<${entity.name}, ${entityIdType}> {
}
