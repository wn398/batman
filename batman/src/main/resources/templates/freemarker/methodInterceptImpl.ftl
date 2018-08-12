<#include "CopyRight.ftl">
package ${project.packageName}.extend.methodInterceptImpl;

import com.rayleigh.core.enums.ResultStatus;
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
import ${project.packageName}.standard.methodIntercept.${entity.name}MethodIntercept;
import org.springframework.stereotype.Component;
@Component
public class ${entity.name}MethodInterceptImpl extends ${entity.name}MethodIntercept {
    @Override
    public ResultWrapper saveBefore(${entity.name} ${entity.name ?uncap_first}){
        //do something of validation
        return getSuccessResultWrapper(${entity.name ?uncap_first});
    }

    @Override
    public ResultWrapper saveAfter(ResultWrapper resultWrapper){
        //do something of result operation
        return resultWrapper;
    }

    @Override
    public ResultWrapper updateBefore(${entity.name} ${entity.name ?uncap_first}){
        //do something of validation
        return getSuccessResultWrapper(${entity.name ?uncap_first});
    }

    @Override
    public ResultWrapper updateAfter(ResultWrapper resultWrapper){
        //do something of result operation
        return resultWrapper;
    }
<#if (entity.mainEntityRelationShips ?size >0)>
    @Override
    public ResultWrapper saveWithRelatedBefore(${entity.name} ${entity.name ?uncap_first}){
        //do something of validation
        return getSuccessResultWrapper(${entity.name ?uncap_first});
    }

    @Override
    public ResultWrapper saveWithRelatedAfter(ResultWrapper resultWrapper){
        //do something of result operation
        return resultWrapper;
    }

    @Override
    public ResultWrapper updateWithRelatedBefore(${entity.name} ${entity.name ?uncap_first}){
        //do something of validation
        return getSuccessResultWrapper(${entity.name ?uncap_first});
    }

    @Override
    public ResultWrapper updateWithRelatedAfter(ResultWrapper resultWrapper){
        //do something of result operation
        return resultWrapper;
    }

    @Override
    public ResultWrapper findOneWithRelationObjBefore(${entity.name}$Relation ${entity.name ?uncap_first}Relation){
        //do something of validation
        return getSuccessResultWrapper(${entity.name ?uncap_first}Relation);
    }

    @Override
    public ResultWrapper findOneWithRelationObjAfter(ResultWrapper resultWrapper){
        //do something of result operation
        return resultWrapper;
    }
</#if>

    @Override
    public ResultWrapper saveWithAssignedIdBefore(${entity.name} ${entity.name ?uncap_first}){
        //do something of validation
        return getSuccessResultWrapper(${entity.name ?uncap_first});
    }

    @Override
    public ResultWrapper saveWithAssignedIdAfter(ResultWrapper resultWrapper){
        //do something of result operation
        return resultWrapper;
    }
<#--页面配置生成的方法-->
<#list entity.methods as method>
    <#if ((method.isInterface !?exists)||(method.isInterface ?exists &&method.isInterface ?boolean))>
    @Override
     public ResultWrapper ${method.methodName}Before(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper){
        //do something of validation
        return getSuccessResultWrapper(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper);
     }
    @Override
     public ResultWrapper ${method.methodName}After(ResultWrapper resultWrapper){
        //do something of result operation
        return resultWrapper;
     }
    </#if>
</#list>

    public ResultWrapper getSuccessResultWrapper(Object obj){
        ResultWrapper resultWrapper = new ResultWrapper();
        resultWrapper.setStatus(ResultStatus.SUCCESS);
        resultWrapper.setData(obj);
        return resultWrapper;
    }
}