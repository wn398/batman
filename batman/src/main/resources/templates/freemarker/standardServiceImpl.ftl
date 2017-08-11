package ${project.packageName}.standard.service.impl;
<#--project,entity,constructSearchMethodUtil-->
import ${project.packageName}.standard.model.*;
import ${project.packageName}.standard.repository.${entity.name}Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import ${project.packageName}.standard.service.${entity.name}Service;
import ${project.packageName}.standard.methodModel.*;
import javax.persistence.EntityManager;
import javax.annotation.Resource;
import javax.persistence.Query;
import java.util.*;
import com.rayleigh.core.model.PageModel;
import com.rayleigh.core.util.StringUtil;
/**
*
*/
public class ${entity.name}ServiceImpl implements ${entity.name}Service {
public Logger logger = LoggerFactory.getLogger(getClass());
@Autowired
private ${entity.name}Repository ${entity.name ?uncap_first}Repository;
@Autowired
private EntityManager entityManager;
@Resource
private JdbcTemplate jdbcTemplate;

    public List<${entity.name}> saveOrUpdate(List<${entity.name}> list){
        return ${entity.name ?uncap_first}Repository.save(list);
    }

    public ${entity.name} saveOrUpdate(${entity.name} ${entity.name ?uncap_first}){
        return ${entity.name ?uncap_first}Repository.save(${entity.name ?uncap_first});
    }
    //保存人为分配id的实体
    public ${entity.name} saveWithAssignedId(${entity.name} ${entity.name ?uncap_first})throws Exception{
         jdbcTemplate.execute(${generatorStringUtil.constructInsertSql(project,entity)});
         logger.info(new StringBuilder("执行本地SQL:").append(${generatorStringUtil.constructInsertSql(project,entity)}).toString());
         return ${entity.name ?uncap_first};
    }

    public void deleteByIds(List<String> ids){
        ids.parallelStream().forEach(id->${entity.name ?uncap_first}Repository.delete(id));
    }

    public void deleteById(String id){
        ${entity.name ?uncap_first}Repository.delete(id);
    }

    public List<${entity.name}> findByIds(List<String> ids){
        return  ${entity.name ?uncap_first}Repository.findAll(ids);
    }

    public ${entity.name} findOne(String id){
        return   ${entity.name ?uncap_first}Repository.findOne(id);
    }

    public List<Object[]> listBySQL(String sql){
        return   ${entity.name ?uncap_first}Repository.listBySQL(sql);
    }

    public Page<${entity.name}> findByAuto(${entity.name} ${entity.name ?uncap_first},Pageable pageable){
        return  ${entity.name ?uncap_first}Repository.findByAuto(${entity.name ?uncap_first},pageable);
    }

    public Page<${entity.name}> findAll(Pageable pageable){
        return   ${entity.name ?uncap_first}Repository.findAll(pageable);
    }

    public List<${entity.name}> findAll(Specification<${entity.name}> specification){
        return   ${entity.name ?uncap_first}Repository.findAll(specification);
    }

    public Page<${entity.name}> findAll(Specification<${entity.name}> specification,Pageable pageable){
        return   ${entity.name ?uncap_first}Repository.findAll(specification,pageable);
    }

    public List<${entity.name}> findAll(){
        return   ${entity.name ?uncap_first}Repository.findAll();
    }

    <#list entity.methods as method>
    public PageModel<${entity.name}$${method.methodName ?cap_first}ResultWrapper> ${method.methodName}(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper){
        <#--定义分页包装结果-->
        PageModel<${entity.name}$${method.methodName ?cap_first}ResultWrapper> pageModel = new PageModel();
        <#--按查询字段结果，按实体分组，构建查询字段-->
        <#assign hql = constructSearchMethodUtil.constructJPQL(method,entity)>
        Query query = entityManager.createQuery("${hql}");
        <#assign entityConditionMap = constructSearchMethodUtil.extractCondition(method.conditionList)>
        <#list entityConditionMap ?keys as key>
            <#--实体名-->
            <#assign entityName = searchDBUtil.getEntityName(key)>
            <#--这个实体下的条件-->
            <#list entityConditionMap[key] as condition>
                <#--获取condition的类型和名字-->
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
                        <#assign fieldType = "String">
                    <#else>
                        <#assign fieldType = "Date">
                    </#if>
                </#if>
                <#--为condition条件设置对应的值，isNull和isNotNull不需要设置-->
                <#if condition.operation == "IsNull" || condition.operation == "IsNotNull">
                <#elseif condition.operation == "Between">
        query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Min",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin());
        query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Max",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax());
                <#elseif condition.operation == "In">
        query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}List",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList());
                <#elseif condition.operation == "Like">
        query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}","%"+${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()+"%");
                <#else>
        query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}());
                </#if>
            </#list>
        </#list>
        <#--最后处理分页信息,如果有传参数，则处理分页参数，否则用默认的2的32次方-->
        //如果需要分页
        if(null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize()>0 &&null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage() >0){
            query.setFirstResult((${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize()-1)*${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage());
            query.setMaxResults(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize());
            <#assign countHql = constructSearchMethodUtil.constructCountJPQL(method,entity)>
            Query countQuery = entityManager.createQuery("${countHql}");
                <#list entityConditionMap ?keys as key>
                <#--实体名-->
                    <#assign entityName = searchDBUtil.getEntityName(key)>
                <#--这个实体下的条件-->
                    <#list entityConditionMap[key] as condition>
                    <#--获取condition的类型和名字-->
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
                                <#assign fieldType = "String">
                            <#else>
                                <#assign fieldType = "Date">
                            </#if>
                        </#if>
                    <#--为condition条件设置对应的值，isNull和isNotNull不需要设置-->
                        <#if condition.operation == "IsNull" || condition.operation == "IsNotNull">
                        <#elseif condition.operation == "Between">
            countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Min",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin());
            countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Max",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax());
                        <#elseif condition.operation == "In">
            countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}List",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList());
                        <#elseif condition.operation == "Like">
            countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}","%"+${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()+"%");
                        <#else>
            countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}());
                        </#if>
                    </#list>
                </#list>
            Long totalRecords = (Long)countQuery.getResultList().get(0);
            pageModel.setTotalRecords(totalRecords);
            Long totalPage;
            if(totalRecords % ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize()==0){
                totalPage = totalRecords/${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize();
            }else{
                totalPage = totalRecords/${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize()+1;
            }
            pageModel.setTotalPage(totalPage);
            pageModel.setCurrentPage(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage());
            pageModel.setPageSize(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize());
        }
        <#--对结果map进行遍历-->
        <#assign searchResultMap = constructSearchMethodUtil.extractResult(method.searchResults)>
        List<Map> list = query.getResultList();
        List<${entity.name}$${method.methodName ?cap_first}ResultWrapper> resultList = new ArrayList<>();
        for(Map map:list){
        ${entity.name}$${method.methodName ?cap_first}ResultWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ResultWrapper= new ${entity.name}$${method.methodName ?cap_first}ResultWrapper();
        <#--开始设置结果-->
        <#list searchResultMap ?keys as key>
        <#--获取到实体名-->
            <#assign entityName = searchDBUtil.getEntityName(key)>
            <#list searchResultMap[key] as result>
                <#--获取结果对应的类型-->
                <#if result.field?exists>
                    <#assign fieldType = result.field.dataType>
                    <#assign fieldName = result.field.name>
                <#else>
                    <#assign fieldName = result.fieldName ?split("_")[1]>
                    <#if fieldName == "id">
                        <#assign fieldType = "String">
                    <#else>
                        <#assign fieldType = "Date">
                    </#if>
                </#if>
        ${entity.name ?uncap_first}$${method.methodName ?cap_first}ResultWrapper.set${entityName}${fieldName ?cap_first}((${fieldType})map.get("${entityName ?uncap_first}${fieldName ?cap_first}"));
            </#list>

        </#list>
        resultList.add(${entity.name ?uncap_first}$${method.methodName ?cap_first}ResultWrapper);
        }
        pageModel.setResults(resultList);
        return pageModel;
    }
    </#list>
}