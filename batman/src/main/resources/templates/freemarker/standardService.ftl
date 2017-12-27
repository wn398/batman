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
/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/
public interface ${entity.name}Service extends BaseService{

    List<${entity.name}> saveOrUpdate(List<${entity.name}> list);

    ${entity.name} saveWithAssignedId(${entity.name} ${entity.name ?uncap_first})throws Exception;

    ${entity.name} saveOrUpdate(${entity.name} ${entity.name ?uncap_first});

    void deleteByIds(List<${entityIdType}> ids);

    ${entity.name} update(${entity.name} ${entity.name ?uncap_first});

    ${entity.name} save(${entity.name} ${entity.name ?uncap_first});

    void deleteById(${entityIdType} id);

    List<${entity.name}> findByIds(List<${entityIdType}> ids);

    List<${entity.name}> findByIds(List<${entityIdType}> ids,String ...propertyNames);

    List<${entity.name}> findByIds(List<${entityIdType}> ids,List<String> propertyNames);

    ${entity.name} findOne(${entityIdType} id);

    ${entity.name} findOne(${entityIdType} id,String ...propertyNames);

    ${entity.name} findOne(${entityIdType} id,List<String> propertyNames);
    //获取带关系的实体
    ${entity.name} findOneWithRelationObj(${entity.name}$Relation ${entity.name ?uncap_first}$Relation);

    List<Object[]> findBySQL(String sql);

    ${entity.name} findOneByProperties(Map<String,Object> map);

    ${entity.name} findOneByProperties(Map<String,Object> map,String ...propertyNames);

    ${entity.name} findOneByProperties(Map<String,Object> map,List<String> propertyNames);

    List<${entity.name}> findByProperties(Map<String,Object> map);

    List<${entity.name}> findByProperties(Map<String,Object> map,String ...propertyNames);

    List<${entity.name}> findByProperties(Map<String,Object> map,List<String> propertyNames);

    Page<${entity.name}> findByProperties(Map<String,Object> map,Pageable pageable);

    Page<${entity.name}> findByProperties(Map<String,Object> map,Pageable pageable,String ...propertyNames);

    Page<${entity.name}> findByProperties(Map<String,Object> map,Pageable pageable,List<String> propertyNames);

    ${entity.name} findOneByProperty(String name,Object value);

    ${entity.name} findOneByProperty(String name,Object value,String ...propertyNames);

    ${entity.name} findOneByProperty(String name,Object value,List<String> propertyNames);

    List<${entity.name}> findByProperty(String name,Object value);

    List<${entity.name}> findByProperty(String name,Object value,String ...propertyNames);

    List<${entity.name}> findByProperty(String name,Object value,List<String> propertyNames);

    Page<${entity.name}> findByProperty(String name,Object value,Pageable pageable);

    Page<${entity.name}> findByProperty(String name,Object value,Pageable pageable,String ...propertyNames);

    Page<${entity.name}> findByProperty(String name,Object value,Pageable pageable,List<String> propertyNames);

    Page<${entity.name}> findByAuto(${entity.name} ${entity.name ?uncap_first},Pageable pageable);

    List<${entity.name}> findAll();

    List<${entity.name}> findAll(String ...propertyNames);

    List<${entity.name}> findAll(List<String> propertyNames);

    Page<${entity.name}> findAll(Pageable pageable);

    Page<${entity.name}> findAll(Pageable pageable,String ...propertyNames);

    Page<${entity.name}> findAll(Pageable pageable,List<String> propertyNames);

    List<${entity.name}> findAll(Specification<${entity.name}> specification);

    List<${entity.name}> findAll(Specification<${entity.name}> specification, String ...propertyNames);

    List<${entity.name}> findAll(Specification<${entity.name}> specification, List<String> propertyNames);

    Page<${entity.name}> findAll(Specification<${entity.name}> specification,Pageable pageable);

    Page<${entity.name}> findAll(Specification<${entity.name}> specification,Pageable pageable,String ...propertyNames);

    Page<${entity.name}> findAll(Specification<${entity.name}> specification,Pageable pageable,List<String> propertyNames);

    List<${entity.name}> findAll(Specification<${entity.name}> specification,Sort sort);

    Integer updateById(${entityIdType} id,String name,Object value);

    Integer updateById(${entityIdType} id,Map<String,Object> updatedNameValues);

    Integer updateByIds(List<${entityIdType}> ids,String name,Object value);

    Integer updateByIds(List<${entityIdType}> ids,Map<String,Object> updatedNameValues);

    Integer updateAll(Specification<${entity.name}> specification,Map<String,Object> updatedNameValues);

    Integer updateAll(Specification<${entity.name}> specification,String name,Object value);
    //根据前两个参数名值，更新所有符合条件的后两个
    Integer updateByProperty(String fieldName,Object fieldValue,String name,Object value);

    Integer updateByProperty(String fieldName,Object fieldValue,Map<String,Object> updatedNameValues);

    Integer updateByProperties(Map<String,Object> conditionMap,String name,Object value);

    Integer updateByProperties(Map<String,Object> conditionMap,Map<String,Object> updatedNameValues);

    Integer deleteByProperty(String name,Object value);

    Integer deleteByProperties(Map<String,Object> conditionMap);

    Integer deleteAll(Specification<${entity.name}> specification);

    Query getBySQL(String sql);

    Query getByHQL(String hql);

    Long getCount(Specification<${entity.name}> specification);

    <#--页面配置生成的方法-->
    <#list entity.methods as method>
        <#if method.isReturnObject ?exists && method.isReturnObject>
    //${method.description}
    Page<${entity.name}> ${method.methodName}(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper);
        <#else>
    //${method.description}
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
     //增加与${relationShip.otherEntity.name}的关系
    String add${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,List<${otherEntityIdType}> ${relationShip.otherEntity.name ?uncap_first}Ids);
     //解除与${relationShip.otherEntity.name}的关系
    String remove${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,List<${otherEntityIdType}> ${relationShip.otherEntity.name ?uncap_first}Ids);
            <#elseif relationShip.relationType == "ManyToOne" ||relationShip.relationType == "OneToOne">
     //重新设置与${relationShip.otherEntity.name}的关系
    String set${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,${otherEntityIdType} ${relationShip.otherEntity.name ?uncap_first}Id2);
    String remove${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,${otherEntityIdType} ${relationShip.otherEntity.name ?uncap_first}Id2);
            </#if>
        </#if>
    </#list>

}

/*
* Generated Code By BatMan on ${.now},@Author-->山猫
*/