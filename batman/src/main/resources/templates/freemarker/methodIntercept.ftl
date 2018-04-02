<#include "CopyRight.ftl">
package ${project.packageName}.standard.methodIntercept;

import com.rayleigh.core.model.ResultWrapper;
import ${project.packageName}.standard.model.${entity.name};
<#list entity.mainEntityRelationShips as relationShip>
import ${project.packageName}.standard.model.${relationShip.otherEntity.name};
import ${project.packageName}.standard.service.${relationShip.otherEntity.name}Service;
import ${project.packageName}.standard.util.${relationShip.otherEntity.name}Util;
</#list>
<#if (entity.methods ?size >0) >
import ${project.packageName}.standard.methodModel.*;
</#if>
import ${project.packageName}.standard.modelRelation.${entity.name}$Relation;
import ${project.packageName}.standard.service.${entity.name}Service;
import ${project.packageName}.standard.util.${entity.name}Util;

public interface ${entity.name}MethodIntercept {
    ResultWrapper saveBefore(${entity.name} ${entity.name ?uncap_first});
    ResultWrapper saveAfter(ResultWrapper resultWrapper);
    ResultWrapper updateBefore(${entity.name} ${entity.name ?uncap_first});
    ResultWrapper updateAfter(ResultWrapper resultWrapper);
<#if (entity.mainEntityRelationShips ?size >0)>
    ResultWrapper saveWithRelatedBefore(${entity.name} ${entity.name ?uncap_first});
    ResultWrapper saveWithRelatedAfter(ResultWrapper resultWrapper);
    ResultWrapper updateWithRelatedBefore(${entity.name} ${entity.name ?uncap_first});
    ResultWrapper updateWithRelatedAfter(ResultWrapper resultWrapper);
    ResultWrapper findOneWithRelationObjBefore(${entity.name}$Relation ${entity.name ?uncap_first}Relation);
    ResultWrapper findOneWithRelationObjAfter(ResultWrapper resultWrapper);
</#if>
    ResultWrapper saveWithAssignedIdBefore(${entity.name} ${entity.name ?uncap_first});
    ResultWrapper saveWithAssignedIdAfter(ResultWrapper resultWrapper);
<#--页面配置生成的方法-->
<#list entity.methods as method>
    <#if ((method.isInterface !?exists)||(method.isInterface ?exists &&method.isInterface ?boolean))>
     ResultWrapper ${method.methodName}Before(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper);
     ResultWrapper ${method.methodName}After(ResultWrapper resultWrapper);
    </#if>
</#list>
}