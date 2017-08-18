package ${project.packageName}.standard.controller;

import ${project.packageName}.standard.model.${entity.name};
<#list entity.mainEntityRelationShips as relationShip>
import ${project.packageName}.standard.model.${relationShip.otherEntity.name};
import ${project.packageName}.standard.service.${relationShip.otherEntity.name}Service;
import ${project.packageName}.standard.util.${relationShip.otherEntity.name}Util;
</#list>
<#if (entity.methods ?size >0) >
import ${project.packageName}.standard.methodModel.*;
</#if>
import ${project.packageName}.standard.modelRelation.${entity.name}$Relation;
import ${project.packageName}.standard.service.${entity.name}Service;
import ${project.packageName}.standard.util.${entity.name}Util;
import com.rayleigh.core.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.service.BaseService;
import com.rayleigh.core.util.BaseModelUtil;
import com.rayleigh.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/

public class ${entity.name}Controller extends BaseController{

@Autowired
private ${entity.name}Service ${entity.name ?uncap_first}Service;
<#list entity.mainEntityRelationShips as relationShip>
    <#if relationShip.mainEntity.name!=relationShip.otherEntity.name>
@Autowired
private ${relationShip.otherEntity.name}Service ${relationShip.otherEntity.name ?uncap_first}Service;
    </#if>
</#list>

@ApiOperation(value = "保存或更新,包括建立关系,有id则更新,没id则保存")
@PostMapping("/doSaveOrUpdate")
@ResponseBody
public ResultWrapper saveOrUpdate(@RequestBody ${entity.name} ${entity.name ?uncap_first}){

    try {
        ${entity.name} ${entity.name ?uncap_first}Result = buildRelation(${entity.name ?uncap_first});
        ${entity.name ?uncap_first}Service.saveOrUpdate(${entity.name ?uncap_first}Result);
        //${entity.name ?uncap_first}Result = ${entity.name}Util.preventMutualRef(${entity.name ?uncap_first}Result);
        return getSuccessResult(${entity.name ?uncap_first}Result);
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first},e.getMessage());
    }
}

@ApiOperation(value = "获取带有关系的数据，需要的关系指定true")
@PostMapping("/findOneWithRelationObj")
@ResponseBody
public ResultWrapper findOneWithRelationObj(@RequestBody ${entity.name}$Relation ${entity.name ?uncap_first}Relation){
    try {
        ${entity.name} ${entity.name ?uncap_first} = ${entity.name ?uncap_first}Service.findOneWithRelationObj(${entity.name ?uncap_first}Relation);
        return getSuccessResult(${entity.name ?uncap_first});
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first}Relation,e.getMessage());
    }
}





@ApiOperation(value = "保存自己分配id的实体,忽略关联实体")
@PostMapping("/saveWithAssignedId")
@ResponseBody
public ResultWrapper saveWithAssignedId(@RequestBody ${entity.name} ${entity.name ?uncap_first}){
    if(StringUtil.isEmpty(${entity.name ?uncap_first}.getId())){
        return getFailureResultAndInfo(${entity.name ?uncap_first},"id不能为空!");
    }
    if(null == ${entity.name ?uncap_first}.getCreateDate()){
        ${entity.name ?uncap_first}.setCreateDate(new Date());
    }

    if(null == ${entity.name ?uncap_first}.getUpdateDate()){
        ${entity.name ?uncap_first}.setUpdateDate(new Date());
    }
    try{
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.saveWithAssignedId(${entity.name ?uncap_first});
        return getSuccessResult(${entity.name ?uncap_first}Result);
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first},e.getMessage());
    }
}

@ApiOperation(value = "根据id查找")
@GetMapping("/findOne/{id}")
@ResponseBody
public ResultWrapper findOneById(@PathVariable("id") String id){
    try{
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.findOne(id);
        return getSuccessResult(${entity.name ?uncap_first}Result);
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(id,e.getMessage());
    }
}

@ApiOperation(value = "根据id删除")
@DeleteMapping("/delete/{id}")
@ResponseBody
public ResultWrapper deleteById(@PathVariable("id") String id){
    try{
        ${entity.name ?uncap_first}Service.deleteById(id);
        return getSuccessResult("delete success!");
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(id,e.getMessage());
    }
}

<#list entity.methods as method>
@ApiOperation(value = "${method.description}")
@PostMapping("/${method.methodName}")
@ResponseBody
public ResultWrapper ${method.methodName}(@RequestBody ${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper){
    try{
        return getSuccessResult(${entity.name ?uncap_first}Service.${method.methodName}(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper));
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper,e.getMessage());
    }
}
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

<#--处理实体之间的关系-->
<#list entity.mainEntityRelationShips as relationShip>
<#if relationShip.mainEntity.name == relationShip.otherEntity.name>

<#else>
    <#if relationShip.relationType == "OneToMany" || relationShip.relationType == "ManyToMany">
    //增加与${relationShip.otherEntity.name}的关系
    @ApiOperation(value = "关联${relationShip.otherEntity.name},只需要传入id即可")
    @PostMapping("/add${relationShip.otherEntity.name}")
    @ResponseBody
    public ResultWrapper add${relationShip.otherEntity.name} (@RequestBody ${entity.name} ${entity.name ?uncap_first}){
        String result = ${entity.name ?uncap_first}Service.add${relationShip.otherEntity.name}(${entity.name ?uncap_first}.getId(),${entity.name ?uncap_first}.get${relationShip.otherEntity.name}List().parallelStream().map(it->it.getId()).collect(Collectors.toList()));
        return getSuccessResult(result);
    }
    //解除与${relationShip.otherEntity.name}的关系
    @ApiOperation(value = "解除关联${relationShip.otherEntity.name},只需要传入id即可")
    @PostMapping("/remove${relationShip.otherEntity.name}")
    @ResponseBody
    public ResultWrapper remove${relationShip.otherEntity.name} (@RequestBody ${entity.name} ${entity.name ?uncap_first}){
        String result = ${entity.name ?uncap_first}Service.remove${relationShip.otherEntity.name}(${entity.name ?uncap_first}.getId(),${entity.name ?uncap_first}.get${relationShip.otherEntity.name}List().parallelStream().map(it->it.getId()).collect(Collectors.toList()));
        return getSuccessResult(result);
    }
    <#elseif relationShip.relationType == "ManyToOne" ||relationShip.relationType == "OneToOne">
    @ApiOperation(value = "设置${relationShip.otherEntity.name}关系,只需要传入id即可")
    @PostMapping("/set${relationShip.otherEntity.name}")
    @ResponseBody
    public ResultWrapper set${relationShip.otherEntity.name} (@RequestBody ${entity.name} ${entity.name ?uncap_first}){
        String result = ${entity.name ?uncap_first}Service.set${relationShip.otherEntity.name}(${entity.name ?uncap_first}.getId(),${entity.name ?uncap_first}.get${relationShip.otherEntity.name}().getId());
        return getSuccessResult(result);
    }

    @ApiOperation(value = "取消${relationShip.otherEntity.name}关系,只需要传入id即可")
    @PostMapping("/remove${relationShip.otherEntity.name}")
    @ResponseBody
    public  ResultWrapper remove${relationShip.otherEntity.name} (@RequestBody ${entity.name} ${entity.name ?uncap_first}){
        String result = ${entity.name ?uncap_first}Service.remove${relationShip.otherEntity.name}(${entity.name ?uncap_first}.getId(),${entity.name ?uncap_first}.get${relationShip.otherEntity.name}().getId());
        return getSuccessResult(result);
    }
    </#if>
</#if>
</#list>

}


/**
* Generated Code By BatMan on ${.now},@Author-->山猫
*/