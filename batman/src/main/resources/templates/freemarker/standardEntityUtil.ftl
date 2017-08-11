package ${project.packageName}.standard.util;

import ${project.packageName}.standard.model.${entity.name};
<#list entity.mainEntityRelationShips as relationShip>
    <#if relationShip.otherEntity.name != entity.name>
import ${project.packageName}.standard.model.${relationShip.otherEntity.name};
    </#if>
</#list>
import java.util.*;
public class ${entity.name}Util {

public static ${entity.name} copySimplePropertyIncludeNullValue(${entity.name} source,${entity.name} target){
    <#list entity.fields as field>
    target.set${field.name ?cap_first}(source.get${field.name ?cap_first}());
    </#list>
    target.setCreateDate(source.getCreateDate());
    target.setVersion(source.getVersion());
    target.setId(source.getId());
    return target;
}

public static ${entity.name} copySimplePropertyNotNullValue(${entity.name} source,${entity.name} target){
    <#list entity.fields as field>
    if(null != source.get${field.name ?cap_first}()){
        target.set${field.name ?cap_first}(source.get${field.name ?cap_first}());
    }
    </#list>
    if(null != source.getCreateDate()){
        target.setCreateDate(source.getCreateDate());
    }
    if(null != source.getVersion()){
        target.setVersion(source.getVersion());
    }
    if(null != source.getId()){
        target.setId(source.getId());
    }
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

}