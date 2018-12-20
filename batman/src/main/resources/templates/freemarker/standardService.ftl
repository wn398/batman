<#include "CopyRight.ftl">
package ${project.packageName}.standard.service;

import com.rayleigh.core.model.PageModel;
import ${project.packageName}.standard.model.${entity.name};
import ${project.packageName}.standard.modelRelation.${entity.name}$Relation;
import com.rayleigh.core.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.Query;
import java.util.*;
<#if (entity.methods ?size >0) >
import ${project.packageName}.standard.methodModel.*;
</#if>
<#--设置主键类型变量-->
<#if entity.primaryKeyType=="String">
    <#assign entityIdType="String">
<#else>
    <#assign entityIdType="Long">
</#if>

public interface ${entity.name}Service extends BaseService<${entity.name},${entityIdType}>{

<#if (entity.mainEntityRelationShips ?size >0)>
    /**
     * 更新连同相关联实体
     * @param ${entity.name ?uncap_first}
     * @return
     */
    ${entity.name} updateWithRelated(${entity.name} ${entity.name ?uncap_first});

    /**
     * 保存连同相关联实体
     * @param ${entity.name ?uncap_first}
     * @return
     */
    ${entity.name} saveWithRelated(${entity.name} ${entity.name ?uncap_first});

    /**
     * 获取连同带关联关系的实体
     * @param ${entity.name ?uncap_first}$Relation
     * @return
     */
    ${entity.name} findOneWithRelationObj(${entity.name}$Relation ${entity.name ?uncap_first}$Relation);
</#if>

    <#--页面配置生成的方法-->
<#list entity.methods as method>
        <#if method.isReturnObject ?exists && method.isReturnObject>
    /**
     * 页面配置生成方法：${method.description}
     * @param ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper
     * @return
     */
    PageModel<${entity.name}> ${method.methodName}(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper);
        <#else>
    /**
     * 页面配置生成方法：${method.description}
     * @param ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper
     * @return
     */
    PageModel<${entity.name}$${method.methodName ?cap_first}ResultWrapper> ${method.methodName}(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper);
        </#if>
</#list>

    <#--处理实体之间的关系的方法-->
<#list entity.mainEntityRelationShips as relationShip>
        <#--设置另一个实体的主键类型-->
        <#if (relationShip.otherEntity.primaryKeyType=="String")>
            <#assign otherEntityIdType="String">
        <#elseif (relationShip.otherEntity.primaryKeyType=="Long")>
            <#assign otherEntityIdType="Long">
        </#if>

        <#if relationShip.mainEntity.name == relationShip.otherEntity.name>
        <#else>
            <#if relationShip.relationType == "OneToMany" || relationShip.relationType == "ManyToMany">
    /**
     * 增加与${relationShip.otherEntity.name}的关系
     * @param ${entity.name ?uncap_first}Id
     * @param  ${relationShip.otherEntity.name ?uncap_first}Ids
     * @return
     */
    String add${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,List<${otherEntityIdType}> ${relationShip.otherEntity.name ?uncap_first}Ids);

    /**
     * 解除与${relationShip.otherEntity.name}的关系
     * @param ${entity.name ?uncap_first}Id
     * @param  ${relationShip.otherEntity.name ?uncap_first}Ids
     * @return
     */
    String remove${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,List<${otherEntityIdType}> ${relationShip.otherEntity.name ?uncap_first}Ids);
            <#elseif relationShip.relationType == "ManyToOne" ||relationShip.relationType == "OneToOne">
    /**
     * 重新设置与${relationShip.otherEntity.name}的关系
     * @param ${entity.name ?uncap_first}Id
     * @param  ${relationShip.otherEntity.name ?uncap_first}Ids
     * @return
     */
    String set${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,${otherEntityIdType} ${relationShip.otherEntity.name ?uncap_first}Id2);

    /**
     * 解除与${relationShip.otherEntity.name}的关系
     * @param ${entity.name ?uncap_first}Id
     * @param  ${relationShip.otherEntity.name ?uncap_first}Ids
     * @return
     */
    String remove${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,${otherEntityIdType} ${relationShip.otherEntity.name ?uncap_first}Id2);
            </#if>
        </#if>
</#list>

}