<#include "CopyRight.ftl">
package ${project.packageName}.standard.service.impl;
<#--project,entity,constructSearchMethodUtil-->
import java.math.BigDecimal;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ${project.packageName}.standard.model.*;
import ${project.packageName}.standard.modelRelation.${entity.name}$Relation;
import ${project.packageName}.standard.repository.${entity.name}Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rayleigh.core.model.SearchMethodConditionModel;
import com.rayleigh.core.model.SearchMethodResultModel;
import com.rayleigh.core.util.SearchMethodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import ${project.packageName}.standard.service.${entity.name}Service;
import ${project.packageName}.standard.util.${entity.name}Util;
<#--导入相关联service-->
<#list entity.mainEntityRelationShips as relationShip>
    <#if relationShip.otherEntity.name != entity.name>
import ${project.packageName}.standard.service.${relationShip.otherEntity.name}Service;
import ${project.packageName}.standard.util.${relationShip.otherEntity.name}Util;
    </#if>
</#list>
<#if (entity.methods ?size >0) >
import ${project.packageName}.standard.methodModel.*;
</#if>
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.EntityManager;
import javax.annotation.Resource;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import com.rayleigh.core.model.PageModel;
import com.rayleigh.core.util.StringUtil;

public class ${entity.name}ServiceImpl implements ${entity.name}Service {
    public Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ${entity.name}Repository ${entity.name ?uncap_first}Repository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ${entity.name}Service ${entity.name ?uncap_first}Service;
    @Resource
    private JdbcTemplate jdbcTemplate;
<#--注入相关联service-->
<#list entity.mainEntityRelationShips as relationShip>
    <#if relationShip.otherEntity.name != entity.name>
    @Autowired
    private ${relationShip.otherEntity.name}Service ${relationShip.otherEntity.name ?uncap_first}Service;
    </#if>
</#list>
<#--用一个变量设置主键类型-->
<#if entity.primaryKeyType=="String">
    <#assign entityIdType="String">
<#elseif entity.primaryKeyType=="Long">
    <#assign entityIdType="Long">
</#if>

    public ${entity.name} save(${entity.name} ${entity.name ?uncap_first}){
        return ${entity.name ?uncap_first}Repository.save(${entity.name ?uncap_first});
    }

<#if (entity.mainEntityRelationShips ?size >0)>
    public ${entity.name} saveWithRelated(${entity.name} ${entity.name ?uncap_first}){
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name}Util.buildRelation(${entity.name ?uncap_first});
        return ${entity.name ?uncap_first}Repository.save(${entity.name ?uncap_first}Result);
    }

    public ${entity.name} updateWithRelated(${entity.name} ${entity.name ?uncap_first}){
        if(null==${entity.name ?uncap_first}.getId()<#if isVersion ==true>||null==${entity.name ?uncap_first}.getVersion()</#if>){
            throw new RuntimeException("更新实体id或version不能为空!");
        }
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name}Util.buildRelation(${entity.name ?uncap_first});
        return ${entity.name ?uncap_first}Repository.save(${entity.name ?uncap_first}Result);
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
</#if>

    public ${entity.name} update(${entity.name} ${entity.name ?uncap_first}){
        if(null==${entity.name ?uncap_first}.getId()<#if isVersion ==true>||null==${entity.name ?uncap_first}.getVersion()</#if>){
            throw new RuntimeException("更新实体id或version不能为空!");
        }
        return ${entity.name ?uncap_first}Repository.save(${entity.name ?uncap_first});
    }

    //保存人为分配id的实体
    public ${entity.name} saveWithAssignedId(${entity.name} ${entity.name ?uncap_first})throws Exception{
         jdbcTemplate.execute(${generatorStringUtil.constructInsertSql(project,entity)});
         logger.info(new StringBuilder("执行本地SQL:").append(${generatorStringUtil.constructInsertSql(project,entity)}).toString());
         return ${entity.name ?uncap_first};
    }

    public void deleteByIds(List<${entityIdType}> ids){
        ids.parallelStream().forEach(id->${entity.name ?uncap_first}Repository.delete(id));
    }

    public void deleteById(${entityIdType} id){
        ${entity.name ?uncap_first}Repository.delete(id);
    }

    public List<${entity.name}> findByIds(List<${entityIdType}> ids){
        return  ${entity.name ?uncap_first}Repository.findAll(ids);
    }

    public List<${entity.name}> findByIds(List<${entityIdType}> ids,List<String> propertyNames){
         CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
         CriteriaQuery<Tuple> tupleCriteriaQuery = criteriaBuilder.createTupleQuery();
         Root<${entity.name}> root = tupleCriteriaQuery.from(${entity.name}.class);
         CriteriaBuilder.In<${entityIdType}> in = criteriaBuilder.in(root.get("id"));
         for(${entityIdType} id:ids){ in.value(id);}
         tupleCriteriaQuery.where(in);
         List<Selection<?>> list = new ArrayList<>();
         propertyNames.stream().forEach(name->list.add(root.get(name).alias(name)));
         tupleCriteriaQuery.multiselect(list);
         TypedQuery<Tuple> tupleTypedQuery = entityManager.createQuery(tupleCriteriaQuery);
         List<Tuple> tupleList = tupleTypedQuery.getResultList();
         List<${entity.name}> resultList = new ArrayList<>();
         for(Tuple tuple:tupleList){
            Map<String,Object> map = new HashMap<String,Object>();
            propertyNames.stream().forEach(name->map.put(name,tuple.get(name)));
            ${entity.name} ${entity.name ?uncap_first} = ${entity.name}Util.setPartProperties(map);
            resultList.add(${entity.name ?uncap_first});
         }
        return resultList;
    }

    public List<${entity.name}> findByIds(List<${entityIdType}> ids,String ...propertyNames){
        return findByIds(ids, Arrays.asList(propertyNames));
    }

    public ${entity.name} findOne(${entityIdType} id){
        return   ${entity.name ?uncap_first}Repository.findOne(id);
    }

    public ${entity.name} findOne(${entityIdType} id,List<String> propertyNames){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleCriteriaQuery = criteriaBuilder.createTupleQuery();
        Root<${entity.name}> root = tupleCriteriaQuery.from(${entity.name}.class);
        tupleCriteriaQuery.where(criteriaBuilder.equal(root.get("id"),id));
        List<Selection<?>> list = new ArrayList<>();
        propertyNames.stream().forEach(name->list.add(root.get(name).alias(name)));
        tupleCriteriaQuery.multiselect(list);
        TypedQuery<Tuple> tupleTypedQuery = entityManager.createQuery(tupleCriteriaQuery);
        List<Tuple> tupleList = tupleTypedQuery.getResultList();
        if(null!=tupleList && tupleList.size()>0){
            Tuple tuple = tupleList.get(0);
            Map<String,Object> map = new HashMap<String,Object>();
            propertyNames.stream().forEach(name->map.put(name,tuple.get(name)));
            ${entity.name} ${entity.name ?uncap_first} = ${entity.name}Util.setPartProperties(map);
            return ${entity.name ?uncap_first};
        }else{
            return null;
        }
    }

    public ${entity.name} findOne(${entityIdType} id,String ...propertyNames){
        return findOne(id,Arrays.asList(propertyNames));
    }

    public List<Object[]> findBySQL(String sql){
        return   ${entity.name ?uncap_first}Repository.listBySQL(sql);
    }

    public ${entity.name} findOneByProperties(Map<String,Object> map){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<${entity.name}> criteriaQuery = criteriaBuilder.createQuery(${entity.name}.class);
        Root<${entity.name}> root = criteriaQuery.from(${entity.name}.class);
        List<Predicate> list = new ArrayList<>();
        map.forEach((key,value)->list.add(criteriaBuilder.equal(root.get(key),value)));
        Predicate[] predicates = new Predicate[list.size()];
        criteriaQuery.where(list.toArray(predicates));
        TypedQuery<${entity.name}> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getSingleResult();
    }

    public ${entity.name} findOneByProperties(Map<String,Object> map,String ...propertyNames){
        return findOneByProperties(map,Arrays.asList(propertyNames));
    }


    public ${entity.name} findOneByProperties(Map<String,Object> map,List<String> propertyNames){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleCriteriaQuery = criteriaBuilder.createTupleQuery();
        Root<${entity.name}> root = tupleCriteriaQuery.from(${entity.name}.class);
        List<Predicate> list = new ArrayList<>();
        map.forEach((key,value)->list.add(criteriaBuilder.equal(root.get(key),value)));
        Predicate[] predicates = new Predicate[list.size()];
        tupleCriteriaQuery.where(list.toArray(predicates));
        List<Selection<?>> selectList = new ArrayList<>();
        propertyNames.stream().forEach(name->selectList.add(root.get(name).alias(name)));
        tupleCriteriaQuery.multiselect(selectList);
        TypedQuery<Tuple> tupleTypedQuery = entityManager.createQuery(tupleCriteriaQuery);
        Tuple tuple = tupleTypedQuery.getSingleResult();
        Map<String,Object> fieldMap = new HashMap<String,Object>();
        propertyNames.stream().forEach(name->fieldMap.put(name,tuple.get(name)));
        ${entity.name} ${entity.name ?uncap_first} = ${entity.name}Util.setPartProperties(fieldMap);
        return ${entity.name ?uncap_first};
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

    public List<${entity.name}> findByProperties(Map<String,Object> map,List<String> propertyNames){
        return findByProperties(map,null,propertyNames).getContent();
    }
    public List<${entity.name}> findByProperties(Map<String,Object> map,String ...propertyNames){
        return findByProperties(map,Arrays.asList(propertyNames));
    }

    public ${entity.name} findOneByProperty(String name,Object value){
        return findOneByProperties(Collections.singletonMap(name,value));
    }

    public ${entity.name} findOneByProperty(String name,Object value,List<String> propertyNames){
        return findOneByProperties(Collections.singletonMap(name,value),propertyNames);
    }

    public ${entity.name} findOneByProperty(String name,Object value,String ...propertyNames){
        return findOneByProperty(name,value,Arrays.asList(propertyNames));
    }

    public List<${entity.name}> findByProperty(String name,Object value){
        return ${entity.name ?uncap_first}Repository.findAll(new Specification<${entity.name}>() {
            @Override
            public Predicate toPredicate(Root<${entity.name}> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(name),value);
            }
        });
    }

    public List<${entity.name}> findByProperty(String name,Object value,List<String> propertyNames){
        return findByProperties(Collections.singletonMap(name,value),propertyNames);
    }

    public List<${entity.name}> findByProperty(String name,Object value,String ...propertyNames){
        return findByProperty(name,value,Arrays.asList(propertyNames));
    }

    public Page<${entity.name}> findByProperty(String name,Object value,Pageable pageAble){
        return ${entity.name ?uncap_first}Repository.findAll(new Specification<${entity.name}>() {
            @Override
            public Predicate toPredicate(Root<${entity.name}> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(name),value);
            }
        },pageAble);
    }

    public Page<${entity.name}> findByProperty(String name,Object value,Pageable pageAble,List<String> propertyNames){
        return findByProperties(Collections.singletonMap(name,value),pageAble,propertyNames);
    }

    public Page<${entity.name}> findByProperty(String name,Object value,Pageable pageAble,String ...propertyNames){
        return findByProperty(name,value,pageAble,Arrays.asList(propertyNames));
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

    public Page<${entity.name}> findByProperties(Map<String,Object> map,Pageable pageable,List<String> propertyNames){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleCriteriaQuery = criteriaBuilder.createTupleQuery();
        Root<${entity.name}> root = tupleCriteriaQuery.from(${entity.name}.class);
        List<Predicate> list = new ArrayList<>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            list.add(criteriaBuilder.equal(root.get(entry.getKey()),entry.getValue()));
        }
        Predicate[] predicates = new Predicate[list.size()];
        tupleCriteriaQuery.where(list.toArray(predicates));

        List<Selection<?>> selectionList = new ArrayList<>();
        propertyNames.stream().forEach(name->selectionList.add(root.get(name).alias(name)));
        tupleCriteriaQuery.multiselect(selectionList);
        TypedQuery<Tuple> tupleTypedQuery = entityManager.createQuery(tupleCriteriaQuery);
        if(null !=pageable){
            tupleTypedQuery.setFirstResult(pageable.getOffset());
            tupleTypedQuery.setMaxResults(pageable.getPageSize());
        }
        List<Tuple> tupleList = tupleTypedQuery.getResultList();
        List<${entity.name}> resultList = new ArrayList<>();
        for(Tuple tuple:tupleList){
            Map<String,Object> filedMap = new HashMap<String,Object>();
            propertyNames.stream().forEach(name->filedMap.put(name,tuple.get(name)));
            resultList.add(${entity.name}Util.setPartProperties(filedMap));
        }

        if(null!=pageable){
            CriteriaQuery<Long> criteriaQueryP = criteriaBuilder.createQuery(Long.class);
            Root<${entity.name}> rootP = criteriaQueryP.from(${entity.name}.class);
            List<Predicate> listP = new ArrayList<>();
            for(Map.Entry<String,Object> entry:map.entrySet()){
                listP.add(criteriaBuilder.equal(root.get(entry.getKey()),entry.getValue()));
            }
            Predicate[] predicates2 = new Predicate[list.size()];
            criteriaQueryP.where(listP.toArray(predicates2));
            criteriaQueryP.select(criteriaBuilder.count(rootP));
            criteriaQueryP.orderBy(Collections.emptyList());
            Long total = entityManager.createQuery(criteriaQueryP).getSingleResult();
            return new PageImpl(resultList,pageable,total);
        }else{
            return new PageImpl(resultList);
        }
    }

    public Page<${entity.name}> findByProperties(Map<String,Object> map,Pageable pageable,String ...propertyNames){
        return findByProperties(map,pageable,Arrays.asList(propertyNames));
    }

    public Page<${entity.name}> findByAuto(${entity.name} ${entity.name ?uncap_first},Pageable pageable){
        return  ${entity.name ?uncap_first}Repository.findByAuto(${entity.name ?uncap_first},pageable);
    }

    public Page<${entity.name}> findAll(Pageable pageable){
        return   ${entity.name ?uncap_first}Repository.findAll(pageable);
    }

    public Page<${entity.name}> findAll(Pageable pageable,List<String> propertyNames){
        return findAll(null,pageable,propertyNames);
    }

    public Page<${entity.name}> findAll(Pageable pageable,String ...propertyNames){
        return findAll(pageable,Arrays.asList(propertyNames));
    }

    public List<${entity.name}> findAll(Specification<${entity.name}> specification){
        return   ${entity.name ?uncap_first}Repository.findAll(specification);
    }

    public List<${entity.name}> findAll(Specification<${entity.name}> specification,List<String> propertyNames){
        return findAll(specification,null,propertyNames).getContent();
    }

    public List<${entity.name}> findAll(Specification<${entity.name}> specification,String ...propertyNames){
        return findAll(specification,Arrays.asList(propertyNames));
    }
    public List<${entity.name}> findAll(Specification<${entity.name}> specification,Sort sort){
        return   ${entity.name ?uncap_first}Repository.findAll(specification,sort);
    }

    public Page<${entity.name}> findAll(Specification<${entity.name}> specification,Pageable pageable){
        return   ${entity.name ?uncap_first}Repository.findAll(specification,pageable);
    }

    public Page<${entity.name}> findAll(Specification<${entity.name}> specification,Pageable pageable,String ...propertyNames){
        return  findAll(specification,pageable,Arrays.asList(propertyNames));
    }

    public Page<${entity.name}> findAll(Specification<${entity.name}> specification,Pageable pageable,List<String> propertyNames){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleCriteriaQuery = criteriaBuilder.createTupleQuery();
        Root<${entity.name}> root = tupleCriteriaQuery.from(${entity.name}.class);
        if(null !=specification) {
            Predicate predicate = specification.toPredicate(root, tupleCriteriaQuery, criteriaBuilder);
            tupleCriteriaQuery.where(predicate);
        }
        List<Selection<?>> list = new ArrayList<>();
        propertyNames.stream().forEach(name->list.add(root.get(name).alias(name)));
        tupleCriteriaQuery.multiselect(list);
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(tupleCriteriaQuery);
        if(null !=pageable){
            typedQuery.setFirstResult(pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
        }
        List<Tuple> tupleList = typedQuery.getResultList();
        List<${entity.name}> ResultList = new ArrayList<>();
        for(Tuple tuple:tupleList){
            Map<String,Object> map = new HashMap();
            propertyNames.stream().forEach(name->map.put(name,tuple.get(name)));
            ResultList.add(${entity.name}Util.setPartProperties(map));
        }
        if(null !=pageable){
            CriteriaQuery<Long> criteriaQueryP = criteriaBuilder.createQuery(Long.class);
            Root<${entity.name}> rootP = criteriaQueryP.from(${entity.name}.class);
            if(null!=specification){
                criteriaQueryP.where(specification.toPredicate(rootP,criteriaQueryP,criteriaBuilder));
            }
            criteriaQueryP.select(criteriaBuilder.count(rootP));
            criteriaQueryP.orderBy(Collections.emptyList());
            Long total = entityManager.createQuery(criteriaQueryP).getSingleResult();
            return new PageImpl(ResultList,pageable,total);
        }else{
            return new PageImpl(ResultList);
        }
    }

    public List<${entity.name}> findAll(){
        return   ${entity.name ?uncap_first}Repository.findAll();
    }

    public List<${entity.name}> findAll(List<String> propertyNames){
        return findAll(null,null,propertyNames).getContent();
    }

    public List<${entity.name}> findAll(String ...propertyNames){
        return findAll(Arrays.asList(propertyNames));
    }

    public Integer updateById(${entityIdType} id<#if isVersion == true>,Long version</#if>,String name,Object value){
        Map<String,Object> conditionMap = new HashMap<>();
        conditionMap.put("id",id);
        <#if isVersion == true>conditionMap.put("version",version);</#if>
        return updateByProperties(conditionMap,Collections.singletonMap(name,value));
    }

    public Integer updateById(${entityIdType} id<#if isVersion == true>,Long version</#if>,Map<String,Object> updatedNameValues){
        Map<String,Object> conditionMap = new HashMap<>();
        conditionMap.put("id",id);
        <#if isVersion == true>conditionMap.put("version",version);</#if>
        return updateByProperties(conditionMap,updatedNameValues);
    }

    public Integer updateAll(Specification<${entity.name}> specification,Map<String,Object> updatedNameValues){
        updatedNameValues.put("updateDate",new Date());
        return ${entity.name ?uncap_first}Repository.updateAll(specification,updatedNameValues);
    }

    public Integer updateAll(Specification<${entity.name}> specification,String name,Object value){
        return updateAll(specification,Collections.singletonMap(name,value));
    }

    public Integer updateByProperty(String fieldName,Object fieldValue,String name,Object value){
        return updateByProperty(fieldName,fieldValue,Collections.singletonMap(name,value));
    }

    public Integer updateByProperty(String fieldName,Object fieldValue,Map<String,Object> updatedNameValues){
        return updateByProperties(Collections.singletonMap(fieldName,fieldValue),updatedNameValues);
    }

    public Integer updateByProperties(Map<String,Object> conditionMap,String name,Object value){
        return updateByProperties(conditionMap,Collections.singletonMap(name,value));
    }

    public Integer updateByProperties(Map<String,Object> conditionMap,Map<String,Object> updatedNameValues){
        return updateAll(new Specification<${entity.name}>() {
        @Override
        public Predicate toPredicate(Root<${entity.name}> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
            List<Predicate> list = new ArrayList<>();
            for(Map.Entry<String,Object> entry:conditionMap.entrySet()){
            list.add(criteriaBuilder.equal(root.get(entry.getKey()),entry.getValue()));
            }
            Predicate[] predicates = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(predicates));
            }
            },updatedNameValues);

    }

    public Integer deleteByProperty(String name,Object value){
        return deleteByProperties(Collections.singletonMap(name,value));
    }

    public Integer deleteByProperties(Map<String,Object> conditionMap){
        return deleteAll(new Specification<${entity.name}>() {
        @Override
        public Predicate toPredicate(Root<${entity.name}> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> list = new ArrayList<>();
            for(Map.Entry<String,Object> entry:conditionMap.entrySet()){
            list.add(criteriaBuilder.equal(root.get(entry.getKey()),entry.getValue()));
            }
            Predicate[] predicates = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(predicates));
            }
            });
    }

    public Integer deleteAll(Specification<${entity.name}> specification){
        return ${entity.name ?uncap_first}Repository.deleteAll(specification);
    }

    public Query getBySQL(String sql){
        return ${entity.name ?uncap_first}Repository.getBySQL(sql);
    }

    public Query getByHQL(String hql){
        return ${entity.name ?uncap_first}Repository.getByHQL(hql);
    }

    public Long getCount(Specification<${entity.name}> specification){
        return ${entity.name ?uncap_first}Repository.getCount(specification);
    }

    <#list entity.methods as method>
    <#--赋值，如果不存在，给个默认值false-->
    <#assign isReturnObject=method.isReturnObject !false>
    <#--赋值是否动态查询-->
    <#assign isDynamicSearch=method.isDynamicSearch !false>
        <#--如果方法定义返回不是对象类型，返回为字段类型，则用JPQL查询-->
    <#if isReturnObject>
        <#assign resultType = entity.name>
    <#else>
        <#assign resultType = entity.name+'$'+method.methodName ?cap_first +'ResultWrapper'>
    </#if>
    <#if isDynamicSearch>
    //${method.description} <#if isDynamicSearch>->[动态查询]<#else>->[静态查询]</#if>
    public PageModel<${resultType}> ${method.methodName}(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper){
        <#--定义分页包装结果-->
        PageModel<${resultType}> pageModel = new PageModel();
        List<String> notNullNames = ${entity.name}$${method.methodName ?cap_first}ParamWrapper.getNotNullParamNames(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper);
        <#assign condtionJSON=constructSearchMethodUtil.getDynamicWhereJSON(method,entity)>
        String conditionJSON = "${condtionJSON}";
        Map<String,JSONObject> conditionMap = (Map<String,JSONObject>)JSONArray.parse(conditionJSON);
        Map<String, SearchMethodConditionModel> map2 = new HashMap<>();
        for(Map.Entry<String,JSONObject> entry:conditionMap.entrySet()){
            SearchMethodConditionModel searchMethodConditionModel =JSONObject.toJavaObject(entry.getValue(),SearchMethodConditionModel.class);
            map2.put(entry.getKey(),searchMethodConditionModel);
        }
        map2 = map2.entrySet().stream().filter(entry->notNullNames.stream().anyMatch(it->it.startsWith(entry.getKey()))).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
        String dynamicConditionHql = SearchMethodUtil.getDynamicConditionStr(map2.values().stream().collect(Collectors.toList()));
        <#assign orderByJSON=constructSearchMethodUtil.getDynamicOrderByJSON(method,entity)>
        String orderByJSON = "${orderByJSON}";
        List<SearchMethodResultModel> resultList = ((List<JSONObject>)JSONArray.parse(orderByJSON)).parallelStream().map(it->JSONObject.toJavaObject(it,SearchMethodResultModel.class)).collect(Collectors.toList());
        String orderByHql = SearchMethodUtil.getOrderByStr(resultList);
        <#--按查询字段结果，按实体分组，构建查询字段-->
        <#if isReturnObject>
            <#assign basicJpql = constructSearchMethodUtil.constructObjectJPQL(method,entity)>
        <#else>
            <#assign basicJpql = constructSearchMethodUtil.constructBasicJPQL(method,entity)>
        </#if>
        String basicJpql = "${basicJpql}";
        if(dynamicConditionHql.trim().equals("")){
            if(basicJpql.trim().endsWith("where")){
                basicJpql = basicJpql.substring(0,basicJpql.indexOf("where"));
            }
        }
        Query query = entityManager.createQuery(basicJpql+dynamicConditionHql+orderByHql);
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
                        <#assign fieldType = entityIdType>
                            <#else>
                                <#assign fieldType = "Date">
                            </#if>
                        </#if>
                    <#--为condition条件设置对应的值，isNull和isNotNull不需要设置-->
                        <#if condition.operation == "IsNull" || condition.operation == "IsNotNull">
                        <#elseif condition.operation == "Between">
        if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax()){
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Min",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin());
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Max",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax());
        }
                        <#elseif condition.operation == "In">
        if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList().size() >0){
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}List",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList());
        }
                        <#elseif condition.operation == "Like">
        if(null !=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()){
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}","%"+${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()+"%");
        }
                        <#else>
        if(null !=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()){
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}());
        }
                        </#if>
                    </#list>
                </#list>
    <#--最后处理分页信息,如果有传参数，则处理分页参数，否则用默认的2的32次方-->
        //如果需要分页
        if(null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize()>0 &&null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage() >0){
            query.setFirstResult((${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage()-1)*${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize());
            query.setMaxResults(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize());
        <#assign baicCountHql = constructSearchMethodUtil.constructBasicCountJPQL(method,entity)>
        String basicCountHql = "${baicCountHql}";
        if(dynamicConditionHql.trim().equals("")){
            if(basicCountHql.trim().endsWith("where")){
                basicCountHql = basicCountHql.substring(0,basicCountHql.indexOf("where"));
            }
        }
            Query countQuery = entityManager.createQuery(basicCountHql+dynamicConditionHql);
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
                        <#assign fieldType = entityIdType>
                            <#else>
                                <#assign fieldType = "Date">
                            </#if>
                        </#if>
                    <#--为condition条件设置对应的值，isNull和isNotNull不需要设置-->
                        <#if condition.operation == "IsNull" || condition.operation == "IsNotNull">
                        <#elseif condition.operation == "Between">
            if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax()){
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Min",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin());
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Max",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax());
            }
                        <#elseif condition.operation == "In">
            if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList().size() >0){
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}List",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList());
            }
                        <#elseif condition.operation == "Like">
            if(null !=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()){
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}","%"+${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()+"%");
            }
                        <#else>
            if(null !=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()){
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}());
            }
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
        <#if !isReturnObject>
        <#--对结果map进行遍历-->
        <#assign searchResultMap = constructSearchMethodUtil.extractResult(method.searchResults)>
        List<Map> list = query.getResultList();
        List<${entity.name}$${method.methodName ?cap_first}ResultWrapper> resultList2 = new ArrayList<>();
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
                        <#assign fieldType = entityIdType>
                    <#else>
                        <#assign fieldType = "Date">
                    </#if>
                </#if>
            ${entity.name ?uncap_first}$${method.methodName ?cap_first}ResultWrapper.set${entityName}${fieldName ?cap_first}((${fieldType})map.get("${entityName ?uncap_first}${fieldName ?cap_first}"));
            </#list>

        </#list>
            resultList2.add(${entity.name ?uncap_first}$${method.methodName ?cap_first}ResultWrapper);
        }
        pageModel.setResults(resultList2);
        <#else >
        List<${resultType}> resultList2 = query.getResultList();
        pageModel.setResults(resultList2);
        </#if>
        return pageModel;
    }
    <#--非动态查询------------------------------------------------------------------------------------------->
    <#else>
     //${method.description} <#if isDynamicSearch>->[动态查询]<#else>->[静态查询]</#if>
    public PageModel<${resultType}> ${method.methodName}(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper){
    <#--定义分页包装结果-->
        PageModel<${resultType}> pageModel = new PageModel();
    <#--按查询字段结果，按实体分组，构建查询字段-->
        <#if !isReturnObject>
            <#assign basicJpql = constructSearchMethodUtil.constructFullFieldsJPQL(method,entity)>
        <#else>
            <#assign basicJpql = constructSearchMethodUtil.constructFullObjectJPQL(method,entity)>
        </#if>
        Query query = entityManager.createQuery("${basicJpql}");
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
                        <#assign fieldType = entityIdType>
                    <#else>
                        <#assign fieldType = "Date">
                    </#if>
                </#if>
            <#--为condition条件设置对应的值，isNull和isNotNull不需要设置-->
                <#if condition.operation == "IsNull" || condition.operation == "IsNotNull">
                <#elseif condition.operation == "Between">
        if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax()){
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Min",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin());
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Max",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax());
        }
                <#elseif condition.operation == "In">
        if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList().size() >0){
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}List",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList());
        }
                <#elseif condition.operation == "Like">
        if(null !=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()){
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}","%"+${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()+"%");
        }
                <#else>
        if(null !=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()){
            query.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}());
        }
                </#if>
            </#list>
        </#list>
    <#--最后处理分页信息,如果有传参数，则处理分页参数，否则用默认的2的32次方-->
        //如果需要分页
        if(null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize()>0 &&null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage() >0){
            query.setFirstResult((${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getCurrentPage()-1)*${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize());
            query.setMaxResults(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.getPageSize());
        <#assign basicCountHql = constructSearchMethodUtil.constructFullCountJPQL(method,entity)>
        Query countQuery = entityManager.createQuery("${basicCountHql}");
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
                        <#assign fieldType = entityIdType>
                    <#else>
                        <#assign fieldType = "Date">
                    </#if>
                </#if>
            <#--为condition条件设置对应的值，isNull和isNotNull不需要设置-->
                <#if condition.operation == "IsNull" || condition.operation == "IsNotNull">
                <#elseif condition.operation == "Between">
            if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin() && null!= ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax()){
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Min",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMin());
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}Max",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}BetweenValue().getMax());
            }
                <#elseif condition.operation == "In">
            if(null!=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList() && ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList().size() >0){
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}List",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}InList());
            }
                <#elseif condition.operation == "Like">
            if(null !=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()){
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}","%"+${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()+"%");
            }
                <#else>
            if(null !=${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}()){
                countQuery.setParameter("${entityName ?uncap_first}${fieldName ?cap_first}",${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entityName}${fieldName ?cap_first}());
            }
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
        <#if !isReturnObject>
        <#--对结果map进行遍历-->
            <#assign searchResultMap = constructSearchMethodUtil.extractResult(method.searchResults)>
        List<Map> list = query.getResultList();
        List<${entity.name}$${method.methodName ?cap_first}ResultWrapper> resultList2 = new ArrayList<>();
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
                            <#assign fieldType = entityIdType>
                        <#else>
                            <#assign fieldType = "Date">
                        </#if>
                    </#if>
                    ${entity.name ?uncap_first}$${method.methodName ?cap_first}ResultWrapper.set${entityName}${fieldName ?cap_first}((${fieldType})map.get("${entityName ?uncap_first}${fieldName ?cap_first}"));
                </#list>

            </#list>
            resultList2.add(${entity.name ?uncap_first}$${method.methodName ?cap_first}ResultWrapper);
        }
        pageModel.setResults(resultList2);
        <#else >
        List<${resultType}> resultList2 = query.getResultList();
        pageModel.setResults(resultList2);
        </#if>
        return pageModel;
    }
    </#if>
    </#list>

    <#--处理实体之间的关系-->
    <#list entity.mainEntityRelationShips as relationShip>
        <#--提前赋值相关联主键类型-->
        <#if (relationShip.otherEntity.primaryKeyType=="String")>
            <#assign otherEntityIdType="String">
        <#elseif (relationShip.otherEntity.primaryKeyType=="Long")>
            <#assign otherEntityIdType="Long">
        </#if>
    <#if relationShip.mainEntity.name == relationShip.otherEntity.name>
    <#else>
        <#if relationShip.relationType == "OneToMany">
    //增加与${relationShip.otherEntity.name}的关系
    @Transactional
    public String add${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,List<${otherEntityIdType}> ${relationShip.otherEntity.name ?uncap_first}Ids){
        if(${relationShip.otherEntity.name ?uncap_first}Ids.size()==1){
            jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' where id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'");
            logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' where id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'").toString());
            return "success";
        } else {
            for(${otherEntityIdType} id:${relationShip.otherEntity.name ?uncap_first}Ids){
                jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' where id = '"+id+"'");
                logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' where id = '"+id+"'").toString());
            }
            return "success";
        }
    }
    //解除与${relationShip.otherEntity.name}的关系
    @Transactional
    public String remove${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,List<${otherEntityIdType}> ${relationShip.otherEntity.name ?uncap_first}Ids){
        if(${relationShip.otherEntity.name ?uncap_first}Ids.size()==1){
            jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = NULL where id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'");
            logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = NULL where id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'").toString());
            return "success";
        } else {
            for(${otherEntityIdType} id:${relationShip.otherEntity.name ?uncap_first}Ids){
                jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = NULL where id = '"+id+"'");
                logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+relationShip.otherEntity.name)} set ${generatorStringUtil.humpToUnderline(entity.name)}_id = NULL where id = '"+id+"'").toString());
            }
            return "success";
        }
    }
    <#elseif relationShip.relationType == "ManyToMany">
    //增加与${relationShip.otherEntity.name}的关系
    @Transactional
    public String add${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,List<${otherEntityIdType}> ${relationShip.otherEntity.name ?uncap_first}Ids){
        if(${relationShip.otherEntity.name ?uncap_first}Ids.size()==1){
            List list = jdbcTemplate.queryForList("select * from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id ='"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'");
            if(null!=list && list.size()>0){

            }else{
                jdbcTemplate.execute("insert into more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} (${generatorStringUtil.humpToUnderline(entity.name)}_id,${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id) values ('"+${entity.name ?uncap_first}Id+"','"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"')");
                logger.info(new StringBuilder("执行本地SQL:").append("insert into more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} (${generatorStringUtil.humpToUnderline(entity.name)}_id,${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id) values ('"+${entity.name ?uncap_first}Id+"','"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'").toString());
            }
            return "success";
        }else{
            for(${otherEntityIdType} id:${relationShip.otherEntity.name ?uncap_first}Ids){
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
    public String remove${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,List<${otherEntityIdType}> ${relationShip.otherEntity.name ?uncap_first}Ids){
        if(${relationShip.otherEntity.name ?uncap_first}Ids.size()==1){
            jdbcTemplate.execute("delete from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'");
            logger.info(new StringBuilder("执行本地SQL:").append("delete from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Ids.get(0)+"'").toString());
            return "success";
        }else{
            for(${otherEntityIdType} id:${relationShip.otherEntity.name ?uncap_first}Ids){
                jdbcTemplate.execute("delete from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+id+"'");
                logger.info(new StringBuilder("执行本地SQL:").append("delete from more_${generatorStringUtil.humpToUnderlineAndOrder(relationShip.mainEntity.name,relationShip.otherEntity.name)} where ${generatorStringUtil.humpToUnderline(entity.name)}_id = '"+${entity.name ?uncap_first}Id+"' and ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+id+"'").toString());
            }
            return "success";
        }
    }
    <#elseif relationShip.relationType == "ManyToOne">
    //重新设置与${relationShip.otherEntity.name}的关系
    @Transactional
    public String set${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,${otherEntityIdType} ${relationShip.otherEntity.name ?uncap_first}Id2){
            jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+entity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Id2+"' where id = '"+${entity.name ?uncap_first}Id+"'");
            logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+entity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = '"+${relationShip.otherEntity.name ?uncap_first}Id2+"' where id = '"+${entity.name ?uncap_first}Id+"'").toString());
            return "success";
    }
    //移除与${relationShip.otherEntity.name}的关系
    @Transactional
    public String remove${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,${otherEntityIdType} ${relationShip.otherEntity.name ?uncap_first}Id2){
        jdbcTemplate.update("update ${generatorStringUtil.humpToUnderline(project.name+entity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = NULL where id = '"+${entity.name ?uncap_first}Id+"'");
        logger.info(new StringBuilder("执行本地SQL:").append("update ${generatorStringUtil.humpToUnderline(project.name+entity.name)} set ${generatorStringUtil.humpToUnderline(relationShip.otherEntity.name)}_id = NULL where id = '"+${entity.name ?uncap_first}Id+"'").toString());
        return "success";
    }
    <#elseif relationShip.relationType == "OneToOne">
    //重新设置与${relationShip.otherEntity.name}的关系
    @Transactional
    public String set${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,${otherEntityIdType} ${relationShip.otherEntity.name ?uncap_first}Id2){
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
    public String remove${relationShip.otherEntity.name} (${entityIdType} ${entity.name ?uncap_first}Id,${otherEntityIdType} ${relationShip.otherEntity.name ?uncap_first}Id2){
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
        if(null!=${entity.name ?uncap_first}.getId()){
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
            if(null!=${relationShip.otherEntity.name ?uncap_first}1.getId()){
                <#--id不为空，需要转换成持久化对象，从数据库中加载出来-->
                ${relationShip.otherEntity.name} db${relationShip.otherEntity.name} = ${relationShip.otherEntity.name ?uncap_first}Service.findOne(${relationShip.otherEntity.name ?uncap_first}1.getId());
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
                if(null != ${relationShip.otherEntity.name ?uncap_first}2 && null != ${relationShip.otherEntity.name ?uncap_first}2.getId()){
                <#--加载持久化对象-->
                ${relationShip.otherEntity.name} db${relationShip.otherEntity.name} = ${relationShip.otherEntity.name ?uncap_first}Service.findOne(${relationShip.otherEntity.name ?uncap_first}2.getId());
                <#--id,version不为空，则是更新，copy基本属性过去-->
                <#if searchDBUtil.isContainField(relationShip.otherEntity.id,"version")==true>if(null!=${relationShip.otherEntity.name ?uncap_first}2.getVersion()){</#if>
                    ${relationShip.otherEntity.name}Util.copySimplePropertyNotNullValue(${relationShip.otherEntity.name ?uncap_first}2,db${relationShip.otherEntity.name});
                <#if searchDBUtil.isContainField(relationShip.otherEntity.id,"version")==true>}</#if>
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
