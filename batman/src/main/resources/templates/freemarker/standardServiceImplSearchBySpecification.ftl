package ${project.packageName}.standard.service.impl;

import ${project.packageName}.standard.model.*;
import ${project.packageName}.standard.repository.${entity.name}Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ${project.packageName}.standard.service.${entity.name}Service;
import ${project.packageName}.standard.methodModel.*;
import javax.persistence.EntityManager;
import java.util.*;
/**
*
*/
public class ${entity.name}ServiceImpl implements ${entity.name}Service {
public Logger logger = LoggerFactory.getLogger(getClass());
@Autowired
private ${entity.name}Repository ${entity.name ?uncap_first}Repository;
@Autowired
private EntityManager entityManager;

    public List<${entity.name}> saveOrUpdate(List<${entity.name}> list){
        return ${entity.name ?uncap_first}Repository.save(list);
    }

    public ${entity.name} saveOrUpdate(${entity.name} ${entity.name ?uncap_first}){
        return ${entity.name ?uncap_first}Repository.save(${entity.name ?uncap_first});
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
    public List<${entity.name}$${method.methodName ?cap_first}ResultWrapper> ${method.methodName}(${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper){
        return ${entity.name ?uncap_first}Repository.findAll(new Specification<${entity.name}>() {
        @Override
        public Predicate toPredicate(Root<${entity.name}> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        List<Predicate> list = new ArrayList<>();
        <#--处理主对象条件都是安排实体分组-->
        <#assign conditionMap=groupUtil.extractCondition(method.conditionList)>
        <#assign resultMap = groupUtil.extractResult(method.searchResults)>

        <#--处理连接条件-->
        <#list conditionMap?keys as key>
            <#if key != entity.id>
                <#assign otherEntityName= groupUtil.getEntityName(key)>
                <#assign relationShip = groupUtil.getRelationTypeByMainOtherEntityId(entity.id,key)>
                <#if relationShip=="OneToMany" ||relationShip=="ManyToMany">
                    Join<${entity.name},${otherEntityName}> ${otherEntityName ?uncap_first}Join = root.join("${otherEntityName ?uncap_first}List", JoinType.INNER);
                <#elseif relationShip=="ManyToOne" || relationShip=="OneToOne">
                    Join<${entity.name},${otherEntityName}> ${otherEntityName ?uncap_first}Join = root.join("${otherEntityName ?uncap_first}", JoinType.INNER);
                </#if>
            </#if>
        </#list>
        <#--获取到按优先级分组的map条件分组-->
        <#assign conditionPriorityMap = groupUtil.groupDescCondition(method.conditionList)>
        <#--按优先级遍历-->
        <#list conditionPriorityMap ?keys as priorityKey>
            List<Predicate> subList = new ArrayList<>();
            <#--遍历同一个优先级下面的条件-->
            <#list conditionPriorityMap[priorityKey] as condition>
                <#assign conditionEntityId = condition.fieldName ?split("_")[0]>
                    <#--获取数据类型和属性名-->
                <#if condition.field?exists>
                    <#assign fieldType = condition.field.dataType>
                    <#assign fieldName = condition.field.name>
                <#else>
                    <#assign fieldName = condition.fieldName ?split("_")[1]>
                    <#if fieldName == "id">
                        <#assign fieldType = "String">
                    <#else>
                        <#assign fieldType = "Date">
                    </#if>
                </#if>
                <#--如果是主对象-->
                <#if conditionEntityId == entity.id>
                    <#if condition.operation =="Between">
                    if(null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entity.name}${fieldName ?cap_first}BetweenValue()){
                    subList.add(criteriaBuilder.${condition.operation ?uncap_first}(root.get("${fieldName}"),${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entity.name}${fieldName ?cap_first}BetweenValue().getMin(),${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entity.name}${fieldName ?cap_first}BetweenValue().getMax()));
                    }
                    <#elseif condition.operation == "In" >
                    if(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entity.name}${fieldName ?cap_first}InList().size()>0){
                    CriteriaBuilder.In<${fieldType}> in = criteriaBuilder.in(root.get("${fieldName}"));
                    for(${fieldType} ${fieldName}:${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entity.name}${fieldName ?cap_first}InList()){
                        in.value(${fieldName});
                    }
                    subList.add(in);
                    }
                    <#elseif condition.operation == "Like">
                    if(null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entity.name}${fieldName ?cap_first}()){
                    subList.add(criteriaBuilder.${condition.operation ?uncap_first}(root.get("${fieldName}"),"%"+${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entity.name}${fieldName ?cap_first}()+"%"));
                    }
                    <#else>
                    if(null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entity.name}${fieldName ?cap_first}()){
                    subList.add(criteriaBuilder.${condition.operation ?uncap_first}(root.get("${fieldName}"),${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${entity.name}${fieldName ?cap_first}()));
                    }
                    </#if>

                <#else><#--非主对象-->
                    <#assign rootJoin = groupUtil.getEntityName(conditionEntityId)+"Join">
                    <#assign otherEntityName2 = groupUtil.getEntityName(conditionEntityId)>
                    <#if condition.operation =="Between">
                    if(null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${otherEntityName2}${fieldName ?cap_first}BetweenValue()){
                    subList.add(criteriaBuilder.${condition.operation ?uncap_first}(${rootJoin ?uncap_first}.get("${fieldName}"),${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${otherEntityName2}${fieldName ?cap_first}BetweenValue().getMin(),${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${otherEntityName2}${fieldName ?cap_first}BetweenValue().getMax()));
                    }
                    <#elseif condition.operation == "In">
                    if(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${otherEntityName2}${fieldName ?cap_first}InList().size()>0){
                    CriteriaBuilder.In<${fieldType}> in = criteriaBuilder.in(root.get("${fieldName}"));
                    for(${fieldType} ${fieldName}:${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${otherEntityName2}${fieldName ?cap_first}InList()){
                        in.value(${fieldName});
                    }
                    subList.add(in);
                    }
                    <#elseif condition.operation == "Like">
                    if(null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${otherEntityName2}${fieldName ?cap_first}()){
                    subList.add(criteriaBuilder.${condition.operation ?uncap_first}(${rootJoin ?uncap_first}.get("${fieldName}"),"%"+${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${otherEntityName2}${fieldName ?cap_first}()+"%"));
                    }
                    <#else>
                    if(null != ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${otherEntityName2}${fieldName ?cap_first}()){
                    subList.add(criteriaBuilder.${condition.operation ?uncap_first}(${rootJoin ?uncap_first}.get("${fieldName}"),${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper.get${otherEntityName2}${fieldName ?cap_first}()));
                    }
                    </#if>
                </#if>
            </#list>
            <#assign logicOperation = groupUtil.getLogicOperationFromCondition(conditionPriorityMap[priorityKey])>
            Predicate[] predicate = new Predicate[list.size()];
            list.add(criteriaBuilder.${logicOperation}(subList.toArray(predicate)));
        </#list>

        <#--设定查询字段-->
        List<Selection<?>> selections = new ArrayList();

        <#--对结果字段进行遍历，按实体类型遍历-->
        <#list resultMap ?keys as key>
            <#--对一个实体的结果进行遍历-->
            <#list resultMap[key] as result>
                <#--获取数据类型-->
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
                <#--主对象-->
                <#if key == entity.id>
                    selections.add(root.get("${fieldName}").alias("${entity.name ?uncap_first}${fieldName ?cap_first}"));
                <#else>
                    <#assign otherName = groupUtil.getEntityName(key)>
                    selections.add(${otherName ?uncap_first}Join.get("${fieldName}").alias("${otherName ?uncap_first}${fieldName ?cap_first}"));

                </#if>
            </#list>
        </#list>
        criteriaQuery.multiselect(selections);
        Predicate[] predicate2 = new Predicate[list.size()];
        criteriaQuery.where(list.toArray(predicate2));
        <#--criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sendTime")));-->
        return criteriaQuery.getRestriction();
        }
        });
    }
    </#list>
}