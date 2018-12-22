<#include "CopyRight.ftl">
<#--构建关联模型 传参：project,entity -->
package ${project.packageName}.base.modelRelation;

import com.rayleigh.core.model.*;
<#--设定主键类型-->
<#if entity.primaryKeyType=="String">
    <#assign entityIdType="String">
<#else>
    <#assign entityIdType="Long">
</#if>

public class ${entity.name}$Relation{
    private ${entityIdType} id;
    <#list entity.mainEntityRelationShips as relation>
        <#if relation.relationType == "OneToMany" || relation.relationType == "ManyToMany">
    private Boolean ${relation.otherEntity.name ?uncap_first}List = false;
        <#else>
    private Boolean ${relation.otherEntity.name ?uncap_first} = false;
        </#if>
    </#list>

    public ${entityIdType} getId(){
        return id;
    }
    public void setId(${entityIdType} id){
        this.id = id;
    }

    <#list entity.mainEntityRelationShips as relation>
        <#if relation.relationType == "OneToMany" || relation.relationType == "ManyToMany">
    public Boolean get${relation.otherEntity.name}List(){
        return ${relation.otherEntity.name ?uncap_first}List;
    }
    public void set${relation.otherEntity.name}List(Boolean is){
        this.${relation.otherEntity.name ?uncap_first}List = is;
    }
        <#else>
    public Boolean get${relation.otherEntity.name}(){
        return ${relation.otherEntity.name ?uncap_first};
    }
    public void set${relation.otherEntity.name}(Boolean is){
        this.${relation.otherEntity.name ?uncap_first} = is;
    }
        </#if>
    </#list>

}

