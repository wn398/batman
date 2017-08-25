package ${project.packageName}.standard.service.impl;
<#--project,entity,constructSearchMethodUtil-->
import ${project.packageName}.standard.model.*;
import ${project.packageName}.standard.modelRelation.${entity.name}$Relation;
import ${project.packageName}.standard.repository.${entity.name}Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import ${project.packageName}.standard.service.${entity.name}Service;
<#if (entity.methods ?size >0) >
import ${project.packageName}.standard.methodModel.*;
</#if>
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Root;
import javax.annotation.Resource;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.*;
import com.rayleigh.core.model.PageModel;
import com.rayleigh.core.util.StringUtil;
/**
* Generated Code By BatMan on ${.now},@Author-->山猫
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

    public ${entity.name} findOneWithRelationObj(${entity.name}$Relation ${entity.name ?uncap_first}$Relation){
        ${entity.name} ${entity.name ?uncap_first} = ${entity.name ?uncap_first}Repository.findOne(${entity.name ?uncap_first}$Relation.getId());
        <#list entity.mainEntityRelationShips as mainR>
            <#if mainR.relationType == "OneToMany" || mainR.relationType == "ManyToMany">
            if(${entity.name ?uncap_first}$Relation.get${mainR.otherEntity.name}List()){
                <#if mainR.relationType == "OneToMany">
                ${entity.name ?uncap_first}.get${mainR.otherEntity.name}List().parallelStream().forEach(it->it.set${entity.name}(null));
                <#else>
                ${entity.name ?uncap_first}.get${mainR.otherEntity.name}List().parallelStream().forEach(it->it.set${entity.name}List(null));
                </#if>
               }
            <#else>
            if(${entity.name ?uncap_first}$Relation.get${mainR.otherEntity.name}()){
                <#if mainR.relationType =="OneToOne">
                ${entity.name ?uncap_first}.get${mainR.otherEntity.name}().set${entity.name}(null);
                <#else>
                ${entity.name ?uncap_first}.get${mainR.otherEntity.name}().set${entity.name}List(null);
                </#if>
            }
            </#if>
        </#list>
        return ${entity.name ?uncap_first};
    }

    public List<Object[]> findBySQL(String sql){
        return   ${entity.name ?uncap_first}Repository.listBySQL(sql);
    }


    public List<${entity.name}> findByProperties(Map<String,Object> map){
        return ${entity.name ?uncap_first}Repository.findAll(new Specification<${entity.name}>() {
            @Override
            public Predicate toPredicate(Root<${entity.name}> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                    for(Map.Entry<String,Object> entry:map.entrySet()){
                        list.add(criteriaBuilder.equal(root.get(entry.getKey()),entry.getValue()));
                    }
                    Predicate[] predicates = new Predicate[list.size()];
                    return criteriaBuilder.and(list.toArray(predicates));
                    }
            });
    }

    public List<${entity.name}> findByProperty(String name,Object value){
        return ${entity.name ?uncap_first}Repository.findAll(new Specification<${entity.name}>() {
            @Override
            public Predicate toPredicate(Root<${entity.name}> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(name),value);
            }
        });
    }

    public Page<${entity.name}> findByProperty(String name,Object value,Pageable pageAble){
        return ${entity.name ?uncap_first}Repository.findAll(new Specification<${entity.name}>() {
            @Override
            public Predicate toPredicate(Root<${entity.name}> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(name),value);
            }
        },pageAble);
    }

    public Page<${entity.name}> findByProperties(Map<String,Object> map,Pageable pageable){
        return ${entity.name ?uncap_first}Repository.findAll(new Specification<${entity.name}>() {
            @Override
            public Predicate toPredicate(Root<${entity.name}> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                    for(Map.Entry<String,Object> entry:map.entrySet()){
                    list.add(criteriaBuilder.equal(root.get(entry.getKey()),entry.getValue()));
                    }
                    Predicate[] predicates = new Predicate[list.size()];
                    return criteriaBuilder.and(list.toArray(predicates));
            }
        },pageable);
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
    <#--处理实体之间的关系-->
    <#list entity.mainEntityRelationShips as relationShip>
    <#if relationShip.mainEntity.name == relationShip.otherEntity.name>

    <#else>
        <#if relationShip.relationType == "OneToMany">
        //增加与${relationShip.otherEntity.name}的关系
        @Transactional
        public String add${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,List<String> ${relationShip.otherEntity.name ?uncap_first}Ids){
            if(${relationShip.otherEntity.name ?uncap_first}Ids.size()==1){
                jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' where id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'");
                logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' where id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'").toString());
                return "success";
            } else {
                for(String id:${relationShip.otherEntity.name ?uncap_first}Ids){
                    jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' where id = '"+id+"'");
                    logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' where id = '"+id+"'").toString());
                }
                return "success";
            }
        }
        //解除与${relationShip.otherEntity.name}的关系
        @Transactional
        public String remove${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,List<String> ${relationShip.otherEntity.name ?uncap_first}Ids){
            if(${relationShip.otherEntity.name ?uncap_first}Ids.size()==1){
                jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = NULL where id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'");
                logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = NULL where id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'").toString());
                return "success";
            } else {
                for(String id:${relationShip.otherEntity.name ?uncap_first}Ids){
                    jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = NULL where id = '"+id+"'");
                    logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = NULL where id = '"+id+"'").toString());
                }
                return "success";
            }
        }
        <#elseif relationShip.relationType == "ManyToMany">
        //增加与${relationShip.otherEntity.name}的关系
        @Transactional
        public String add${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,List<String> ${relationShip.otherEntity.name ?uncap_first}Ids){
            if(${relationShip.otherEntity.name ?uncap_first}Ids.size()==1){
                List list = jdbcTemplate.queryForList("select * from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id ='"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'");
                if(null!=list && list.size()>0){

                }else{
                    jdbcTemplate.execute("insert into more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} (${generatorStringUtil.humpToUnderline(entity.name)}_id,${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id) values ('"+${entity.name ?uncap_first}Id+"','"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"')");
                    logger.info(new StringBuilder("执行本地SQL:").append("insert into more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} (${generatorStringUtil.humpToUnderline(entity.name)}_id,${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id) values ('"+${entity.name ?uncap_first}Id+"','"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'").toString());
                }
                return "success";
            }else{
                for(String id:${relationShip.otherEntity.name ?uncap_first}Ids){
                List list = jdbcTemplate.queryForList("select * from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id ='"+id+"'");
                if(null!=list && list.size()>0){

                    }else{
                        jdbcTemplate.execute("insert into more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} (${generatorStringUtil.humpToUnderline(entity.name)}_id,${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id) values ('"+${entity.name ?uncap_first}Id+"','"+id+"')");
                        logger.info(new StringBuilder("执行本地SQL:").append("insert into more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} (${generatorStringUtil.humpToUnderline(entity.name)}_id,${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id) values ('"+${entity.name ?uncap_first}Id+"','"+id+"')").toString());
                    }
                }
                return "success";
            }
        }
        //解除与${relationShip.otherEntity.name}的关系
        @Transactional
        public String remove${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,List<String> ${relationShip.otherEntity.name ?uncap_first}Ids){
            if(${relationShip.otherEntity.name ?uncap_first}Ids.size()==1){
                jdbcTemplate.execute("delete from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'");
                logger.info(new StringBuilder("执行本地SQL:").append("delete from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'").toString());
                return "success";
            }else{
                for(String id:${relationShip.otherEntity.name ?uncap_first}Ids){
                    jdbcTemplate.execute("delete from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+id+"'");
                    logger.info(new StringBuilder("执行本地SQL:").append("delete from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+id+"'").toString());
                }
                return "success";
            }
        }
        <#elseif relationShip.relationType == "ManyToOne">
        //重新设置与${relationShip.otherEntity.name}的关系
        @Transactional
        public String set${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,String ${relationShip.otherEntity.name ?uncap_first}Id2){
                jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+entity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Id2+"' where id = '"+${entity.name ?uncap_first}Id+"'");
                logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+entity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Id2+"' where id = '"+${entity.name ?uncap_first}Id+"'").toString());
                return "success";
        }
        //移除与${relationShip.otherEntity.name}的关系
        @Transactional
        public String remove${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,String ${relationShip.otherEntity.name ?uncap_first}Id2){
            jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+entity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = NULL where id = '"+${entity.name ?uncap_first}Id+"'");
            logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+entity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = NULL where id = '"+${entity.name ?uncap_first}Id+"'").toString());
            return "success";
        }
        <#elseif relationShip.relationType == "OneToOne">
        //重新设置与${relationShip.otherEntity.name}的关系
        @Transactional
        public String set${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,String ${relationShip.otherEntity.name ?uncap_first}Id2){
            List list = jdbcTemplate.queryForList("select * from one_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"'");
            if(list.size()>0){
                jdbcTemplate.update("update one_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Id2+"' where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"'");
                logger.info(new StringBuilder("执行本地SQL:").append("update one_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Id2+" where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"'").toString());
                return "success";
            }else{
                jdbcTemplate.execute("insert into one_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} (${generatorStringUtil.humpToUnderline(entity.name)}_id,${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id) values ('"+${entity.name ?uncap_first}Id+"','"+${relationShip.otherEntity.name ?uncap_first}Id2+"')");
                logger.info(new StringBuilder("执行本地SQL:").append("insert into one_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} (${generatorStringUtil.humpToUnderline(entity.name)}_id,${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id) values ('"+${entity.name ?uncap_first}Id+"','"+${relationShip.otherEntity.name ?uncap_first}Id2+"')").toString());
                return "success";
            }
        }
        //移除与${relationShip.otherEntity.name}的关系
        @Transactional
        public String remove${relationShip.otherEntity.name} (String ${entity.name ?uncap_first}Id,String ${relationShip.otherEntity.name ?uncap_first}Id2){
            jdbcTemplate.execute("delete from one_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Id2+"'");
            logger.info(new StringBuilder("执行本地SQL:").append("delete from one_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Id2+"'").toString());
            return "success";
        }
        </#if>
    </#if>
    </#list>

            <#--建立关系的辅助类，保存或更新前转换成持久化对象，保证子对象对父对象的引用都是持久化的-->
            private  ${entity.name} buildRelation(${entity.name} ${entity.name ?uncap_first}){
            <#--设置主对象结果，如果有id则查询出持久化对象作为基准结果，然后copy基本属性过去，如果没有id则查看对象属性里有没有需要转变成持久化的对象-->
            ${entity.name} ${entity.name ?uncap_first}Result;
                if(!StringUtil.isEmpty(${entity.name ?uncap_first}.getId())){
            ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.findOne(${entity.name ?uncap_first}.getId());
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
                    if(!StringUtil.isEmpty(${relationShip.otherEntity.name ?uncap_first}1.getId())){
                <#--id不为空，需要转换成持久化对象，从数据库中加载出来-->
                ${relationShip.otherEntity.name} db${relationShip.otherEntity.name} = ${relationShip.otherEntity.name ?uncap_first}Service.findOne(${relationShip.otherEntity.name ?uncap_first}1.getId());
                <#--id,version都不为空，可能更新基本属性，进行copy-->
                    if(null!=${relationShip.otherEntity.name ?uncap_first}1.getVersion()){
                ${relationShip.otherEntity.name}Util.copySimplePropertyNotNullValue(${relationShip.otherEntity.name ?uncap_first}1,db${relationShip.otherEntity.name});
                    }
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
                    if(null != ${relationShip.otherEntity.name ?uncap_first}2 && !StringUtil.isEmpty(${relationShip.otherEntity.name ?uncap_first}2.getId())){
                <#--加载持久化对象-->
                ${relationShip.otherEntity.name} db${relationShip.otherEntity.name} = ${relationShip.otherEntity.name ?uncap_first}Service.findOne(${relationShip.otherEntity.name ?uncap_first}2.getId());
                <#--id,version不为空，则是更新，copy基本属性过去-->
                    if(null!=${relationShip.otherEntity.name ?uncap_first}2.getVersion()){
                ${relationShip.otherEntity.name}Util.copySimplePropertyNotNullValue(${relationShip.otherEntity.name ?uncap_first}2,db${relationShip.otherEntity.name});
                    }
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
                    }else{
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
}

/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/