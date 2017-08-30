package ${project.packageName}.standard.service;

import com.rayleigh.core.model.PageModel;
import ${project.packageName}.standard.model.${entity.name};
import ${project.packageName}.standard.modelRelation.${entity.name}$Relation;
import com.rayleigh.core.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.*;
<#if (entity.methods ?size >0) >
import ${project.packageName}.standard.methodModel.*;
</#if>
/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/
public interface ${entity.name}Service extends BaseService{

    List<${entity.name}> saveOrUpdate(List<${entity.name}> list);

    ${entity.name} saveWithAssignedId(${entity.name} ${entity.name ?uncap_first})throws Exception;

    ${entity.name} saveOrUpdate(${entity.name} ${entity.name ?uncap_first});

    void deleteByIds(List<String> ids);

    ${entity.name} update(${entity.name} ${entity.name ?uncap_first});

    ${entity.name} save(${entity.name} ${entity.name ?uncap_first});

    void deleteById(String id);

    List<${entity.name}> findByIds(List<String> ids);

    ${entity.name} findOne(String id);
    //获取带关系的实体
    ${entity.name} findOneWithRelationObj(${entity.name}$Relation ${entity.name ?uncap_first}$Relation);

    List<Object[]> findBySQL(String sql);

    List<${entity.name}> findByProperties(Map<String,Object> map);

    Page<${entity.name}> findByProperties(Map<String,Object> map,Pageable pageable);

    List<${entity.name}> findByProperty(String name,Object value);

    Page<${entity.name}> findByProperty(String name,Object value,Pageable pageable);

    Page<${entity.name}> findByAuto(${entity.name} ${entity.name ?uncap_first},Pageable pageable);

    Page<${entity.name}> findAll(Pageable pageable);

    List<${entity.name}> findAll(Specification<${entity.name}> specification);

    Page<${entity.name}> findAll(Specification<${entity.name}> specification,Pageable pageable);

    List<${entity.name}> findAll(Specification<${entity.name}> specification,Sort sort);

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
        <#if relationShip.mainEntity.name == relationShip.otherEntity.name>
        <#else>
            <#if relationShip.relationType == "OneToMany" || relationShip.relationType == "ManyToMany">
     //增加与${relationShip.otherEntity.name}的关系
    String add${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,List<String> ${relationShip.otherEntity.name ?uncap_first}Ids);
     //解除与${relationShip.otherEntity.name}的关系
    String remove${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,List<String> ${relationShip.otherEntity.name ?uncap_first}Ids);
            <#elseif relationShip.relationType == "ManyToOne" ||relationShip.relationType == "OneToOne">
     //重新设置与${relationShip.otherEntity.name}的关系
    String set${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,String ${relationShip.otherEntity.name ?uncap_first}Id2);
    String remove${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,String ${relationShip.otherEntity.name ?uncap_first}Id2);
            </#if>
        </#if>
    </#list>
}

/*
* Generated Code By BatMan on ${.now},@Author-->山猫
*/