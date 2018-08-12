<#include "CopyRight.ftl">
<#--<!--构建方法的结果模型  需要传入的参数 project, entity, method,searchDBUtil&ndash;&gt;-->
package ${project.packageName}.standard.methodModel;
import java.math.BigDecimal;
import java.util.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
<#if entity.primaryKeyType=="String">
    <#assign entityIdType="String">
<#else>
    <#assign entityIdType="Long">
</#if>
/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/
//@ApiModel("${method.description} 方法结果包装类")
public class ${entity.name}$${method.methodName ?cap_first}ResultWrapper{

<#list method.searchResults as result>
    <#--获取结果字段所在的实体类-->
    <#assign entityName=searchDBUtil.getEntityName(result.fieldName ?split("_")[0])>
    <#assign entityDescription=searchDBUtil.getEntityDescription(result.fieldName ?split("_")[0])>
    <#--获取数据类型-->
    <#if result.field?exists>
        <#assign fieldType = result.field.dataType>
        <#assign fieldName = result.field.name>
    <#else>
        <#assign fieldName = result.fieldName ?split("_")[1]>
        <#if fieldName == "id">
            <#assign fieldType = entityIdType>
        <#else>
            <#assign fieldType = "Date">
        </#if>
    </#if>
    <#if fieldType == "Date">
@JSONField(format="yyyy-MM-dd HH:mm:ss")
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    </#if>
@ApiModelProperty("${entityDescription}->${result.field.description}")
private ${fieldType} ${entityName ?uncap_first}${fieldName ?cap_first};
</#list>

<#list  method.searchResults as result>
    <#--获取结果字段所在的实体类-->
    <#assign entityName=searchDBUtil.getEntityName(result.fieldName ?split("_")[0])>
    <#--获取数据类型-->
    <#if result.field?exists>
        <#assign fieldType = result.field.dataType>
        <#assign fieldName = result.field.name>
    <#else>
        <#assign fieldName = result.fieldName ?split("_")[1]>
        <#if fieldName == "id">
            <#assign fieldType = entityIdType>
        <#else>
            <#assign fieldType = "Date">
        </#if>
    </#if>
public ${fieldType} get${entityName}${fieldName ?cap_first}(){
    <#if fieldType == "Date">
    if(null!= ${entityName ?uncap_first}${fieldName ?cap_first}) {
        return (Date)${entityName ?uncap_first}${fieldName ?cap_first}.clone();
    }else{
        return null;
    }
    <#else>
    return ${entityName ?uncap_first}${fieldName ?cap_first};
    </#if>
}

public void set${entityName}${fieldName ?cap_first}(${fieldType} ${entityName ?uncap_first}${fieldName ?cap_first}){
    <#if fieldType == "Date">
    this.${entityName ?uncap_first}${fieldName ?cap_first} = (Date)${entityName ?uncap_first}${fieldName ?cap_first}.clone();
    <#else>
    this.${entityName ?uncap_first}${fieldName ?cap_first} = ${entityName ?uncap_first}${fieldName ?cap_first};
    </#if>
}

</#list>

}
