<#include "CopyRight.ftl">
package ${project.packageName}.base.util;

import com.rayleigh.core.util.SpringContextUtils;
import com.rayleigh.core.util.StringUtil;
import com.rayleigh.core.model.NameValueType;
import com.rayleigh.core.enums.*;
import ${project.packageName}.base.model.${entity.name};
import ${project.packageName}.base.service.${entity.name}Service;
<#list entity.mainEntityRelationShips as relationShip>
    <#if relationShip.otherEntity.name != entity.name>
import ${project.packageName}.base.model.${relationShip.otherEntity.name};
import ${project.packageName}.base.service.${relationShip.otherEntity.name}Service;
    </#if>
</#list>
<#if entity.primaryKeyType=="String">
    <#assign entityIdType="String">
<#else>
    <#assign entityIdType="Long">
</#if>
import java.math.BigDecimal;
import java.util.*;

public class ${entity.name}Util {

public static ${entity.name} copySimplePropertyIncludeNullValue(${entity.name} source,${entity.name} target){
    <#list entity.fields as field>
    target.set${field.name ?cap_first}(source.get${field.name ?cap_first}());
    </#list>
    return target;
}

public static ${entity.name} copySimplePropertyNotNullValue(${entity.name} source,${entity.name} target){
    <#list entity.fields as field>
    if(null != source.get${field.name ?cap_first}()){
        target.set${field.name ?cap_first}(source.get${field.name ?cap_first}());
    }
    </#list>
    return target;
}

public static ${entity.name} preventMutualRef(${entity.name} ${entity.name ?uncap_first}){
<#list entity.mainEntityRelationShips as relationShip>
    <#--对象类型-->
    <#if relationShip.relationType =="OneToOne">
    ${relationShip.otherEntity.name} ${relationShip.otherEntity.name ?uncap_first}1 = ${entity.name ?uncap_first}.get${relationShip.otherEntity.name}();
    if(null != ${relationShip.otherEntity.name ?uncap_first}1 && null != ${relationShip.otherEntity.name ?uncap_first}1.get${entity.name}()){
        ${relationShip.otherEntity.name ?uncap_first}1.set${entity.name}(null);
    }
    </#if>
    <#--对象类型 ManyToOne-->
    <#if relationShip.relationType == "ManyToOne">
    ${relationShip.otherEntity.name} ${relationShip.otherEntity.name ?uncap_first}2 = ${entity.name ?uncap_first}.get${relationShip.otherEntity.name}();
    if(null != ${relationShip.otherEntity.name ?uncap_first}2 && ${relationShip.otherEntity.name ?uncap_first}2.get${entity.name}List().size() > 0){
        ${relationShip.otherEntity.name ?uncap_first}2.set${entity.name}List(null);
    }
    </#if>
    <#--列表类型 一对多-->
    <#if relationShip.relationType == "OneToMany">
    List<${relationShip.otherEntity.name}> ${relationShip.otherEntity.name ?uncap_first}List3 = ${entity.name ?uncap_first}.get${relationShip.otherEntity.name}List();
    for(${relationShip.otherEntity.name} ${relationShip.otherEntity.name ?uncap_first}3 :${relationShip.otherEntity.name ?uncap_first}List3){
        if(null != ${relationShip.otherEntity.name ?uncap_first}3.get${entity.name}()){
            ${relationShip.otherEntity.name ?uncap_first}3.set${entity.name}(null);
        }
    }
    </#if>
    <#--列表类型  多对多-->
    <#if relationShip.relationType == "ManyToMany">
    List<${relationShip.otherEntity.name}> ${relationShip.otherEntity.name ?uncap_first}List4 = ${entity.name ?uncap_first}.get${relationShip.otherEntity.name}List();
    for(${relationShip.otherEntity.name} ${relationShip.otherEntity.name ?uncap_first}4 : ${relationShip.otherEntity.name ?uncap_first}List4){
        if(null !=${relationShip.otherEntity.name ?uncap_first}4.get${entity.name}List() && ${relationShip.otherEntity.name ?uncap_first}4.get${entity.name}List().size()>0){
            ${relationShip.otherEntity.name ?uncap_first}4.set${entity.name}List(null);
        }
    }
    </#if>
</#list>
    return ${entity.name ?uncap_first};
}

<#--建立关系的辅助类，保存或更新前转换成持久化对象，保证子对象对父对象的引用都是持久化的-->
public static  ${entity.name} buildRelation(${entity.name} ${entity.name ?uncap_first}){
<#--设置主对象结果，如果有id则查询出持久化对象作为基准结果，然后copy基本属性过去，如果没有id则查看对象属性里有没有需要转变成持久化的对象-->
    ${entity.name} ${entity.name ?uncap_first}Result;
    if(null != ${entity.name ?uncap_first}.getId()){
        ${entity.name ?uncap_first}Result = ((${entity.name}Service)SpringContextUtils.getBean("${entity.name ?uncap_first}ExtServiceImpl")).findOne(${entity.name ?uncap_first}.getId());
        ${entity.name}Util.copySimplePropertyNotNullValue(${entity.name ?uncap_first}, ${entity.name ?uncap_first}Result);
    }else{
        ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first};
    }
<#--处理对象关系，对象或list-->
<#list entity.mainEntityRelationShips as relationShip>
<#--如果是一对一，或多对一，在主对象中表现为一个对象-->
    <#if relationShip.relationType == "OneToOne" || relationShip.relationType == "ManyToOne">
    <#--获取此对象-->
    ${relationShip.otherEntity.name} ${relationShip.otherEntity.name ?uncap_first}1 = ${entity.name ?uncap_first}.get${relationShip.otherEntity.name}();
    <#--如果此对象不为空-->
    if(null != ${relationShip.otherEntity.name ?uncap_first}1){
    <#--并且id不为空-->
        if(null != ${relationShip.otherEntity.name ?uncap_first}1.getId()){
    <#--id不为空，需要转换成持久化对象，从数据库中加载出来-->
        ${relationShip.otherEntity.name} db${relationShip.otherEntity.name} = ((${relationShip.otherEntity.name}Service)SpringContextUtils.getBean("${relationShip.otherEntity.name ?uncap_first}ExtServiceImpl")).findOne(${relationShip.otherEntity.name ?uncap_first}1.getId());
    <#--id,version都不为空，可能更新基本属性，进行copy-->
        <#if searchDBUtil.isContainField(relationShip.otherEntity.id,"version")==true>if(null!=${relationShip.otherEntity.name ?uncap_first}1.getVersion()){</#if>
        ${relationShip.otherEntity.name}Util.copySimplePropertyNotNullValue(${relationShip.otherEntity.name ?uncap_first}1,db${relationShip.otherEntity.name});
        <#if searchDBUtil.isContainField(relationShip.otherEntity.id,"version")==true>}</#if>
    <#--进行关系绑定-->
    <#--如果是多对一，则反向是list，进行add-->
        <#if relationShip.relationType == "ManyToOne">
        db${relationShip.otherEntity.name}.get${entity.name}List().add(${entity.name ?uncap_first}Result);
        </#if>
    <#--如果是一对一，则直接设置【2017-7-28】同多对多一样，一对一的配置双方都是主导者，所以并不需要人为配置关系，注释掉-->
    <#--<#if relationShip.relationType == "OneToOne">-->
    <#--db${relationShip.otherEntity.name}.set${entity.name}(${entity.name ?uncap_first}Result);-->
    <#--</#if>-->
        ${entity.name ?uncap_first}Result.set${relationShip.otherEntity.name}(db${relationShip.otherEntity.name});
    }else{
    <#--id为空，不需要加载持久化对象，只需要绑定关系-->
    <#--如果是多对一，反向是list，进行add-->
        <#if relationShip.relationType == "ManyToOne">
        ${relationShip.otherEntity.name ?uncap_first}1.get${entity.name}List().add(${entity.name ?uncap_first}Result);
        </#if>
    <#--如果是一对一，直接set 【2017-7-28】同多对多一样，一对一的配置双方都是主导者，所以并不需要人为配置关系，注释掉-->
    <#--<#if relationShip.relationType == "OneToOne">-->
    <#--${relationShip.otherEntity.name ?uncap_first}1.set${entity.name}(${entity.name ?uncap_first}Result);-->
    <#--</#if>-->
    <#--上面已经把对象关系绑定好了，现在设置到主对象里-->
        ${entity.name ?uncap_first}Result.set${relationShip.otherEntity.name}(${relationShip.otherEntity.name ?uncap_first}1);
        }
    }
    </#if>

<#--如果是一对多，或多对多，在主对象中表现为一个List，其中list中的每个对象都要同主对象作关系绑定-->
    <#if relationShip.relationType == "OneToMany" || relationShip.relationType == "ManyToMany">
    <#--获取到这个list-->
    List<${relationShip.otherEntity.name}> ${relationShip.otherEntity.name ?uncap_first}List = ${entity.name ?uncap_first}.get${relationShip.otherEntity.name}List();
    <#--如果list不为空就会进行处理-->
    if(null != ${relationShip.otherEntity.name ?uncap_first}List && ${relationShip.otherEntity.name ?uncap_first}List.size() > 0){
    <#--作为新的结果list存放器-->
        List<${relationShip.otherEntity.name}> result${relationShip.otherEntity.name}List = new ArrayList();
        for(${relationShip.otherEntity.name} ${relationShip.otherEntity.name ?uncap_first}2:${relationShip.otherEntity.name ?uncap_first}List){
    <#--如果id不为空，则需要从数据库中加载持久化对象出来-->
            if(null != ${relationShip.otherEntity.name ?uncap_first}2 && null !=${relationShip.otherEntity.name ?uncap_first}2.getId()){
    <#--加载持久化对象-->
                ${relationShip.otherEntity.name} db${relationShip.otherEntity.name} = ((${relationShip.otherEntity.name}Service)SpringContextUtils.getBean("${relationShip.otherEntity.name ?uncap_first}ExtServiceImpl")).findOne(${relationShip.otherEntity.name ?uncap_first}2.getId());
    <#--id,version不为空，则是更新，copy基本属性过去-->
                <#if searchDBUtil.isContainField(relationShip.otherEntity.id,"version")==true>if(null!=${relationShip.otherEntity.name ?uncap_first}2.getVersion()){</#if>
                    ${relationShip.otherEntity.name}Util.copySimplePropertyNotNullValue(${relationShip.otherEntity.name ?uncap_first}2,db${relationShip.otherEntity.name});
                <#if searchDBUtil.isContainField(relationShip.otherEntity.id,"version")==true> }</#if>
    <#--跟主对象绑定关系-->
    <#--如果是多对多，表现为list增加,【2017-7-28】多对多配置，本身就是双方主导，因此不需要我们人为增加关系进去，否则会导致关系多插入一条，因此注释下面-->
    <#--<#if relationShip.relationType == "ManyToMany">-->
    <#--db${relationShip.otherEntity.name}.get${entity.name}List().add(${entity.name ?uncap_first}Result);-->
    <#--</#if>-->
    <#--如果是一对多，则反向是多对一，直接set-->
        <#if relationShip.relationType == "OneToMany">
                db${relationShip.otherEntity.name}.set${entity.name}(${entity.name ?uncap_first}Result);
        </#if>
    <#--一切绑定或更新基本属性后，把它放到结果list中-->
            result${relationShip.otherEntity.name}List.add(db${relationShip.otherEntity.name});
            }else if(null != ${relationShip.otherEntity.name ?uncap_first}2){
    <#--如果id为空，则说明是新增的对象并直接关联,无需多数据库加载，直接绑定关系-->
    <#--如果是多对多，说明是list，可以add 【2017-7-28】多对多配置，本身就是双方主导，因此不需要我们人为增加关系进去，否则会导致关系多插入一条，因此注释下面-->
    <#--<#if relationShip.relationType == "ManyToMany">-->
    <#--${relationShip.otherEntity.name ?uncap_first}2.get${entity.name}List().add(${entity.name ?uncap_first}Result);-->
    <#--</#if>-->
    <#--如果是一对多，则直接set-->
        <#if relationShip.relationType == "OneToMany">
                ${relationShip.otherEntity.name ?uncap_first}2.set${entity.name}(${entity.name ?uncap_first}Result);
        </#if>
    <#--把处理结果增加到结果list中-->
                result${relationShip.otherEntity.name}List.add(${relationShip.otherEntity.name ?uncap_first}2);
                }
            }
    <#--清除原来的list，增加我们处理好的结果list-->
        ${entity.name ?uncap_first}Result.get${relationShip.otherEntity.name}List().clear();
        ${entity.name ?uncap_first}Result.get${relationShip.otherEntity.name}List().addAll(result${relationShip.otherEntity.name}List);
    }
    </#if>
</#list>
    return ${entity.name ?uncap_first}Result;
}

//通过字段名和字段值设置实体，只设置实体的一部分属性
public static ${entity.name} setPartProperties(Map<String,Object> propertyValueMap){
    ${entity.name} ${entity.name ?uncap_first} = new ${entity.name}();
    <#list entity.fields as field>
    if(null != propertyValueMap.get("${field.name}")){
        ${entity.name ?uncap_first}.set${field.name ?cap_first}((${field.dataType})propertyValueMap.get("${field.name}"));
    }
    </#list>
    return ${entity.name ?uncap_first};
}

//获取实体类的属性名和值对应的map
public static Map<String,Object> getNotNullPropertiesValueMap(${entity.name} ${entity.name ?uncap_first}){
    Map<String,Object> map = new HashMap<String,Object>();
    <#list entity.fields as field>
    if(null != ${entity.name ?uncap_first}.get${field.name ?cap_first}()){
        map.put("${field.name ?uncap_first}",${entity.name ?uncap_first}.get${field.name ?cap_first}());
    }
    </#list>
    return map;
}

//获取实体类所有的属性名和值对应的map
public static Map<String,Object> getAllPropertiesValueMap(${entity.name} ${entity.name ?uncap_first}){
    Map<String,Object> map = new HashMap<String,Object>();
<#list entity.fields as field>
    map.put("${field.name ?uncap_first}",${entity.name ?uncap_first}.get${field.name ?cap_first}());
</#list>
    return map;
}

//获取实体类的属性名和值及对应字段类型
public static List<NameValueType> getNameValueTypeList(${entity.name} ${entity.name ?uncap_first}){
    List<NameValueType> list = new ArrayList<>();
<#list entity.fields as field>
    if(null != ${entity.name ?uncap_first}.get${field.name ?cap_first}()){
        NameValueType nameValueType = new NameValueType();
        nameValueType.setName("${field.name ?uncap_first}");
        nameValueType.setValue(${entity.name ?uncap_first}.get${field.name ?cap_first}());
        nameValueType.setDataType(DataType.${field.dataType});
        list.add(nameValueType);
    }
</#list>
    return list;
}

//获取实体类所有简单属性值列表
public static List<String> getPropertyNames(){
    List<String> list = new ArrayList();
    <#list entity.fields as field>
    list.add("${field.name ?uncap_first}");
    </#list>
    return list;
}

//通过属性名获取属性类型
public static DataType getPropertyDataType(String propertyName){
    <#list entity.fields as field>
    if("${field.name}".equals(propertyName)){
        return DataType.${field.dataType};
    }
    </#list>
    return null;
}

//获取表名
public static String getTableName(){
    return <#if entity.tableName ?exists && (entity.tableName ?length>0) >"${entity.tableName}"<#else><#if entity.addPrefix ==true>"${GeneratorStringUtil.humpToUnderline(module.name+entity.name)}"</#if><#if entity.addPrefix==false>"${GeneratorStringUtil.humpToUnderline(entity.preFix! +entity.name)}"</#if></#if>;
}

}
