package ${project.packageName}.standard.model;

import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.util.StringUtil;
import com.rayleigh.core.annotation.FieldInfo;
import org.hibernate.validator.constraints.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;


/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/
@FieldInfo("${entity.description}")
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "${GeneratorStringUtil.humpToUnderline(project.name+entity.name)}",
    indexes = {
     @Index(name = "rk_${entity.name}_createDate", columnList = "createDate")
    ,@Index(name = "rk_${entity.name}_updateDate", columnList = "updateDate")
    <#list entity.fields as field>
        <#if field.isIndex>
    ,@Index(name = "rk_${entity.name}_${field.name}", columnList = "${field.name}")
        </#if>
    </#list>
}
)
public class ${entity.name} extends BaseModel {

<#--生成普通属性-->
<#list entity.fields as field>
@FieldInfo("${field.description}")
    <#if field.dataType == "Date">
@JSONField(format="yyyy-MM-dd HH:mm:ss")
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
@Temporal(TemporalType.TIMESTAMP)
    </#if>
<#--长度设置只对字符串类型有用-->
    <#if (field.dataType == "String" && field.size ?exists)>
@Column(nullable = ${field.isNull ?string("true","false")},length=${field.size ?c}<#if field.isUnique>,unique = true</#if>)
    <#else>
@Column(nullable = ${field.isNull ?string("true","false")}<#if field.isUnique>,unique = true</#if>)
    </#if>
<#if (field.validMessage ?exists && field.validMessage ?length>0)>
    <#list field.validMessage ?split("||") as validMessage>
${validMessage}
    </#list>
</#if>
    <#--处理默认值-->
    <#if (field.defaultValue ?exists && field.defaultValue ?length>0)>
        <#if field.dataType == "Date">
private ${field.dataType} ${field.name} = StringUtil.stringToDate("${field.defaultValue}");
        <#elseif field.dataType == "Boolean">
private ${field.dataType} ${field.name} = new Boolean("${field.defaultValue}");
        <#elseif field.dataType == "BigDecimal">
private ${field.dataType} ${field.name} = new BigDecimal("${field.defaultValue}");
        <#elseif field.dataType == "String">
private ${field.dataType} ${field.name} = "${field.defaultValue}";
        <#elseif field.dataType == "Long">
private ${field.dataType} ${field.name} = "${field.defaultValue}L";
        <#else>
private ${field.dataType} ${field.name} = ${field.defaultValue};
        </#if>
    <#else>
private ${field.dataType} ${field.name};
    </#if>
</#list>

<#--生成关联关系属性-->
<#list entity.mainEntityRelationShips as mainR>

${"@"+mainR.relationType}(<#if mainR.isLazyFetch>fetch = FetchType.LAZY,<#else>fetch = FetchType.EAGER,</#if>cascade={CascadeType.PERSIST<#if mainR.isCascadeDelete>,CascadeType.REMOVE</#if>,CascadeType.MERGE}<#if mainR.relationType=="OneToMany">,mappedBy="${mainR.mainEntity.name ?uncap_first}"</#if>)
    <#if mainR.relationType=="ManyToOne">
@JoinColumn(name = "${GeneratorStringUtil.humpToUnderline(mainR.otherEntity.name)}_id")
    </#if>
    <#if mainR.relationType="ManyToMany">
@JoinTable(name = "more_${GeneratorStringUtil.humpToUnderlineAndOrder(mainR.mainEntity.name,mainR.otherEntity.name)}",joinColumns = @JoinColumn(name = "${GeneratorStringUtil.humpToUnderline(mainR.mainEntity.name)}_id",referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "${GeneratorStringUtil.humpToUnderline(mainR.otherEntity.name)}_id",referencedColumnName = "id"))
    </#if>
    <#if mainR.relationType="OneToOne">
@JoinTable(name = "one_${GeneratorStringUtil.humpToUnderlineAndOrder(mainR.mainEntity.name,mainR.otherEntity.name)}",joinColumns = @JoinColumn(name = "${GeneratorStringUtil.humpToUnderline(mainR.mainEntity.name)}_id",referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "${GeneratorStringUtil.humpToUnderline(mainR.otherEntity.name)}_id",referencedColumnName = "id"))
    </#if>
private <#if mainR.relationType =="ManyToMany" || mainR.relationType == "OneToMany">List<${mainR.otherEntity.name}> ${mainR.otherEntity.name ?uncap_first}List = new ArrayList<>();
          <#else>${mainR.otherEntity.name} ${mainR.otherEntity.name ?uncap_first};
        </#if>

</#list>

<#--普通属性，get,set方法-->
<#list entity.fields as field>
public ${field.dataType} get${field.name ?cap_first}(){
    <#if field.dataType=="Date">
    if(null!= ${field.name}) {
        return (Date)${field.name}.clone();
    }else{
        return null;
    }
    <#else>
    return ${field.name};
    </#if>
}

public void set${field.name ?cap_first}(${field.dataType} ${field.name}){
    <#if field.dataType=="Date">
    this.${field.name} = (Date)${field.name}.clone();
    <#else>
    this.${field.name} = ${field.name};
    </#if>
}

</#list>

<#--对象类型set,get方法-->
<#list entity.mainEntityRelationShips as mainR>
    <#if mainR.relationType == "OneToMany" || mainR.relationType == "ManyToMany">
public List<${mainR.otherEntity.name}> get${mainR.otherEntity.name}List(){
    return ${mainR.otherEntity.name ?uncap_first}List;
}
    <#else>
public ${mainR.otherEntity.name} get${mainR.otherEntity.name}(){
    return ${mainR.otherEntity.name ?uncap_first};
}
    </#if>

    <#if mainR.relationType == "OneToMany">
public void set${mainR.otherEntity.name}List (List<${mainR.otherEntity.name}> ${mainR.otherEntity.name ?uncap_first}List){
    <#--if(null!=${mainR.otherEntity.name ?uncap_first}List){-->
        <#--${mainR.otherEntity.name ?uncap_first}List.parallelStream().forEach(<#if mainR.relationType == "OneToMany">it->it.set${mainR.mainEntity.name}(this));<#else>it->it.get${mainR.mainEntity.name}List().add(this));</#if>-->
    <#--}-->
    this.${mainR.otherEntity.name ?uncap_first}List = ${mainR.otherEntity.name ?uncap_first}List;
}
    <#else>
        <#if mainR.relationType == "ManyToMany">
public void set${mainR.otherEntity.name}List (List<${mainR.otherEntity.name}> ${mainR.otherEntity.name ?uncap_first}List){
        this.${mainR.otherEntity.name ?uncap_first}List = ${mainR.otherEntity.name ?uncap_first}List;
}
        <#else>
public void set${mainR.otherEntity.name} (${mainR.otherEntity.name} ${mainR.otherEntity.name ?uncap_first}){
    this.${mainR.otherEntity.name ?uncap_first} = ${mainR.otherEntity.name ?uncap_first};
}
        </#if>
    </#if>

</#list>


}

/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/