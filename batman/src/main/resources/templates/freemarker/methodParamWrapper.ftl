<#include "CopyRight.ftl">
<#--构建方法的参数模型 传参：project,entity,method-->
package ${project.packageName}.base.methodModel;
import com.rayleigh.core.enums.DataType;
import com.rayleigh.core.model.*;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
<#if entity.primaryKeyType=="String">
    <#assign entityIdType="String">
<#else>
    <#assign entityIdType="Long">
</#if>
//@ApiModel("${method.description} 方法入参包装类")
public class ${entity.name}$${method.methodName ?cap_first}ParamWrapper{

<#--分页参数-->
@ApiModelProperty("每页条数")
private Integer pageSize;
@ApiModelProperty("请求第几页")
private Integer currentPage;
<#--属性-->
    <#list method.conditionList as condition>
        <#--获取结果字段所在的实体类-->
        <#assign entityName=searchDBUtil.getEntityName(condition.fieldName ?split("_")[0])>
        <#assign entityDescription=searchDBUtil.getEntityDescription(condition.fieldName ?split("_")[0])>
        <#--获取数据类型-->
        <#if condition.field?exists>
            <#if condition.operation == "IsNull" || condition.operation == "IsNotNull">
                <#assign fieldType = "Boolean">
                <#assign fieldName = condition.field.name+condition.operation>
            <#else>
                <#assign fieldType = condition.field.dataType>
                <#assign fieldName = condition.field.name>
            </#if>
        <#else>
            <#assign fieldName = condition.fieldName ?split("_")[1]>
            <#if fieldName == "id">
                <#assign fieldType = entityIdType>
            <#else>
                <#assign fieldType = "Date">
            </#if>
        </#if>


        <#if condition.operation =="Between">
            <#if fieldType =="Date">
@ApiModelProperty("${entityDescription}->${condition.field.description}.日期间隔")
private  DateBetweenValue ${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue;
            <#else>
@ApiModelProperty("${entityDescription}->${condition.field.description}.大小间隔")
private  BetweenValue<${fieldType}> ${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue;
            </#if>
        <#elseif condition.operation == "In">
@ApiModelProperty("${entityDescription}->${condition.field.description}.in集合")
private List<${fieldType}> ${entityName ?uncap_first}${fieldName ?cap_first}InList = new ArrayList<>();
        <#else>
            <#if fieldType == "Date">
@ApiModelProperty("${entityDescription}->${condition.field.description}")
@JSONField(format="yyyy-MM-dd HH:mm:ss")
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
private ${fieldType} ${entityName ?uncap_first}${fieldName ?cap_first};
                <#else>
@ApiModelProperty("${entityDescription}->${condition.field.description}")
private ${fieldType} ${entityName ?uncap_first}${fieldName ?cap_first};
            </#if>
        </#if>
    </#list>

<#--分页参数 getter and setter-->
public Integer getPageSize() {
    return pageSize;
}

public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
}

public Integer getCurrentPage() {
    return currentPage;
}

public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
}

<#--get,set方法-->
    <#list method.conditionList as condition>
    <#--获取结果字段所在的实体类-->
        <#assign entityName=searchDBUtil.getEntityName(condition.fieldName ?split("_")[0])>
    <#--获取数据类型-->
        <#if condition.field?exists>
            <#if condition.operation == "IsNull" || condition.operation == "IsNotNull">
                <#assign fieldType = "Boolean">
                <#assign fieldName = condition.field.name+condition.operation>
            <#else>
                <#assign fieldType = condition.field.dataType>
                <#assign fieldName = condition.field.name>
            </#if>
        <#else>
            <#assign fieldName = condition.fieldName ?split("_")[1]>
            <#if fieldName == "id">
                <#assign fieldType = entityIdType>
            <#else>
                <#assign fieldType = "Date">
            </#if>
        </#if>

        <#if condition.operation == "Between">
            <#if fieldType=="Date">
public DateBetweenValue get${entityName}${fieldName ?cap_first}BetweenValue(){
    return  ${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue;
}

public void set${entityName}${fieldName ?cap_first}BetweenValue(DateBetweenValue ${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue){
    this.${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue = ${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue;
}
            <#else>
public BetweenValue<${fieldType}> get${entityName}${fieldName ?cap_first}BetweenValue(){
    return  ${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue;
}

public void set${entityName}${fieldName ?cap_first}BetweenValue(BetweenValue<${fieldType}> ${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue){
    this.${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue = ${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue;
}
            </#if>
        <#elseif condition.operation == "In">
public List<${fieldType}> get${entityName}${fieldName ?cap_first}InList(){
    return ${entityName ?uncap_first}${fieldName ?cap_first}InList;
}

public void set${entityName}${fieldName ?cap_first}InList(List<${fieldType}> ${entityName ?uncap_first}${fieldName ?cap_first}InList){
    this.${entityName ?uncap_first}${fieldName ?cap_first}InList = ${entityName ?uncap_first}${fieldName ?cap_first}InList;
}
        <#else>

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

        </#if>
    </#list>
//获取非空的参数名称
public static List<String> getNotNullParamNames(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper){
     List<String> result = new ArrayList();
     <#list method.conditionList as condition>
     <#--获取结果字段所在的实体类-->
         <#assign entityName=searchDBUtil.getEntityName(condition.fieldName ?split("_")[0])>
     <#--获取数据类型-->
         <#if condition.field?exists>
             <#if condition.operation == "IsNull" || condition.operation == "IsNotNull">
                 <#assign fieldType = "Boolean">
                 <#assign fieldName = condition.field.name+condition.operation>
             <#else>
                 <#assign fieldType = condition.field.dataType>
                 <#assign fieldName = condition.field.name>
             </#if>
         <#else>
             <#assign fieldName = condition.fieldName ?split("_")[1]>
             <#if fieldName == "id">
                 <#assign fieldType = entityIdType>
             <#else>
                 <#assign fieldType = "Date">
             </#if>
         </#if>


         <#if condition.operation =="Between">
             <#if fieldType =="Date">
    if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax()){
        result.add("${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue");
    }
             <#else>
    if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax()){
        result.add("${entityName ?uncap_first}${fieldName ?cap_first}BetweenValue");
    }
             </#if>
         <#elseif condition.operation == "In">
    if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList().size() >0){
        result.add("${entityName ?uncap_first}${fieldName ?cap_first}InList");
    }
         <#else>
    if(null !=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()){
        result.add("${entityName ?uncap_first}${fieldName ?cap_first}");
    }
         </#if>
     </#list>
    return result;
    }
}
