<#include "CopyRight.ftl">
package ${project.packageName}.standard.controller;

import com.rayleigh.core.model.PageModel;
import ${project.packageName}.standard.model.${entity.name};
import ${project.packageName}.standard.methodIntercept.${entity.name}MethodIntercept;
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

<#--设置主键类型-->
<#if entity.primaryKeyType=="String">
    <#assign entityIdType="String">
<#else>
    <#assign entityIdType="Long">
</#if>
public class ${entity.name}Controller extends BaseController{

@Autowired
private ${entity.name}Service ${entity.name ?uncap_first}Service;
<#list entity.mainEntityRelationShips as relationShip>
    <#if relationShip.mainEntity.name!=relationShip.otherEntity.name>
@Autowired
private ${relationShip.otherEntity.name}Service ${relationShip.otherEntity.name ?uncap_first}Service;
    </#if>
</#list>
@Autowired
private ${entity.name}MethodIntercept ${entity.name ?uncap_first}MethodIntercept;

@ApiOperation(value = "✿自动生成✿->保存实体,不包括相关联实体")
@PostMapping("/doSave")
@ResponseBody
public ResultWrapper<${entity.name}> save(@RequestBody ${entity.name} ${entity.name ?uncap_first}){
    if(${entity.name ?uncap_first}MethodIntercept.saveBefore(${entity.name ?uncap_first}).getStatus()!=ResultStatus.SUCCESS){
        return ${entity.name ?uncap_first}MethodIntercept.saveBefore(${entity.name ?uncap_first});
    }
    try {
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.save(${entity.name ?uncap_first});
        return ${entity.name ?uncap_first}MethodIntercept.saveAfter(getSuccessResult(${entity.name ?uncap_first}Result));
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first},e.getMessage());
    }
}

@ApiOperation(value = "✿自动生成✿->更新实体,不包括相关联实体")
@PostMapping("/doUpdate")
@ResponseBody
public ResultWrapper<${entity.name}> update(@RequestBody ${entity.name} ${entity.name ?uncap_first}){
    if(${entity.name ?uncap_first}MethodIntercept.updateBefore(${entity.name ?uncap_first}).getStatus()!=ResultStatus.SUCCESS){
        return ${entity.name ?uncap_first}MethodIntercept.updateBefore(${entity.name ?uncap_first});
    }
    try {
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.update(${entity.name ?uncap_first});
        return ${entity.name ?uncap_first}MethodIntercept.updateAfter(getSuccessResult(${entity.name ?uncap_first}Result));
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first},e.getMessage());
    }
}
<#if (entity.mainEntityRelationShips ?size >0)>
@ApiOperation(value = "✿自动生成✿->保存，包括相关关联实体")
@PostMapping("/doSaveWithRelated")
@ResponseBody
public ResultWrapper<${entity.name}> saveWithRelated(@RequestBody ${entity.name} ${entity.name ?uncap_first}){
    if(${entity.name ?uncap_first}MethodIntercept.saveWithRelatedBefore(${entity.name ?uncap_first}).getStatus()!=ResultStatus.SUCCESS){
        return ${entity.name ?uncap_first}MethodIntercept.saveWithRelatedBefore(${entity.name ?uncap_first});
    }
    try {
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.save(${entity.name ?uncap_first});
        ${entity.name ?uncap_first}Result = ${entity.name}Util.preventMutualRef(${entity.name ?uncap_first}Result);
        return ${entity.name ?uncap_first}MethodIntercept.saveWithRelatedAfter(getSuccessResult(${entity.name ?uncap_first}Result));
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first},e.getMessage());
    }
}

@ApiOperation(value = "✿自动生成✿->更新,包括相关联实体")
@PostMapping("/doUpdateWithRelated")
@ResponseBody
public ResultWrapper<${entity.name}> updateWithRelated(@RequestBody ${entity.name} ${entity.name ?uncap_first}){
    if(${entity.name ?uncap_first}MethodIntercept.updateWithRelatedBefore(${entity.name ?uncap_first}).getStatus()!=ResultStatus.SUCCESS){
        return ${entity.name ?uncap_first}MethodIntercept.updateWithRelatedBefore(${entity.name ?uncap_first});
    }
    try {
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.save(${entity.name ?uncap_first});
        ${entity.name ?uncap_first}Result = ${entity.name}Util.preventMutualRef(${entity.name ?uncap_first}Result);
        return ${entity.name ?uncap_first}MethodIntercept.updateWithRelatedAfter(getSuccessResult(${entity.name ?uncap_first}Result));
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first},e.getMessage());
    }
}

@ApiOperation(value = "✿自动生成✿->获取带有关系的数据，需要的关系指定true")
@PostMapping("/findOneWithRelationObj")
@ResponseBody
public ResultWrapper<${entity.name}> findOneWithRelationObj(@RequestBody ${entity.name}$Relation ${entity.name ?uncap_first}Relation){
    if(${entity.name ?uncap_first}MethodIntercept.findOneWithRelationObjBefore(${entity.name ?uncap_first}Relation).getStatus()!=ResultStatus.SUCCESS){
        return ${entity.name ?uncap_first}MethodIntercept.findOneWithRelationObjBefore(${entity.name ?uncap_first}Relation);
    }
    try {
        ${entity.name} ${entity.name ?uncap_first} = ${entity.name ?uncap_first}Service.findOneWithRelationObj(${entity.name ?uncap_first}Relation);
        return ${entity.name ?uncap_first}MethodIntercept.findOneWithRelationObjAfter(getSuccessResult(${entity.name ?uncap_first}));
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first}Relation,e.getMessage());
    }
}
</#if>

@ApiOperation(value = "✿自动生成✿->保存自己分配id的实体,忽略关联实体")
@PostMapping("/saveWithAssignedId")
@ResponseBody
public ResultWrapper<${entity.name}> saveWithAssignedId(@RequestBody ${entity.name} ${entity.name ?uncap_first}){
    if(${entity.name ?uncap_first}MethodIntercept.saveWithAssignedIdBefore(${entity.name ?uncap_first}).getStatus()!=ResultStatus.SUCCESS){
        return ${entity.name ?uncap_first}MethodIntercept.saveWithAssignedIdBefore(${entity.name ?uncap_first});
    }
    if(null == ${entity.name ?uncap_first}.getId()){
        return getFailureResultAndInfo(${entity.name ?uncap_first},"id不能为空!");
    }
    <#if isCreateDate == true>
    if(null == ${entity.name ?uncap_first}.getCreateDate()){
        ${entity.name ?uncap_first}.setCreateDate(new Date());
    }
    </#if>
    <#if isUpdateDate == true>
    if(null == ${entity.name ?uncap_first}.getUpdateDate()){
        ${entity.name ?uncap_first}.setUpdateDate(new Date());
    }
    </#if>
    try{
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.saveWithAssignedId(${entity.name ?uncap_first});
        return ${entity.name ?uncap_first}MethodIntercept.saveWithAssignedIdAfter(getSuccessResult(${entity.name ?uncap_first}Result));
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first},e.getMessage());
    }
}

@ApiOperation(value = "✿自动生成✿->根据id查找")
@GetMapping("/findOne/{id}")
@ResponseBody
public ResultWrapper<${entity.name}> findOneById(@PathVariable("id") ${entityIdType} id){
    try{
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.findOne(id);
        return getSuccessResult(${entity.name ?uncap_first}Result);
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(id,e.getMessage());
    }
}
<#--不展现到接口里
@ApiOperation(value = "根据id删除")
@DeleteMapping("/delete/{id}")
@ResponseBody
public ResultWrapper deleteById(@PathVariable("id") ${entityIdType} id){
    try{
        ${entity.name ?uncap_first}Service.deleteById(id);
        return getSuccessResult("delete success!");
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(id,e.getMessage());
    }
}
-->

<#--页面配置生成的方法-->
<#list entity.methods as method>
    <#assign isDynamicSearch=method.isDynamicSearch !false>
    <#assign isReturnObject=method.isReturnObject !false>

    <#if ((method.isInterface !?exists)||(method.isInterface ?exists &&method.isInterface ?boolean))>
@ApiOperation(value = "✿自动生成✿->${method.description} <#if isDynamicSearch>->[动态查询]<#else>->[静态查询]</#if><#if isReturnObject>[主对象]<#else>[字段包装]</#if>")
@PostMapping("/${method.methodName}")
@ResponseBody
        <#if method.isReturnObject ?exists && method.isReturnObject>
public ResultWrapper<PageModel<${entity.name}>> ${method.methodName}(@RequestBody ${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper){
        <#else>
public ResultWrapper<PageModel<${entity.name}$${method.methodName ?cap_first}ResultWrapper>> ${method.methodName}(@RequestBody ${entity.name}$${method.methodName ?cap_first}ParamWrapper ${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper){
        </#if>
    if(${entity.name ?uncap_first}MethodIntercept.${method.methodName}Before(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper).getStatus()!=ResultStatus.SUCCESS){
        return ${entity.name ?uncap_first}MethodIntercept.${method.methodName}Before(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper);
    }
    try{
        return ${entity.name ?uncap_first}MethodIntercept.${method.methodName}After(getSuccessResult(${entity.name ?uncap_first}Service.${method.methodName}(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper)));
    }catch (Exception e){
        e.printStackTrace();
        return getFailureResultAndInfo(${entity.name ?uncap_first}$${method.methodName ?cap_first}ParamWrapper,e.getMessage());
    }
}
    </#if>
</#list>


<#--处理实体之间的关系-->
<#list entity.mainEntityRelationShips as relationShip>
<#if relationShip.mainEntity.name == relationShip.otherEntity.name>

<#else>
    <#if relationShip.relationType == "OneToMany" || relationShip.relationType == "ManyToMany">
    //增加与${relationShip.otherEntity.name}的关系
    @ApiOperation(value = "✿自动生成✿->关联${relationShip.otherEntity.name},只需要传入id即可")
    @PostMapping("/add${relationShip.otherEntity.name}")
    @ResponseBody
    public ResultWrapper<String> add${relationShip.otherEntity.name} (@RequestBody ${entity.name} ${entity.name ?uncap_first}){
        String result = ${entity.name ?uncap_first}Service.add${relationShip.otherEntity.name}(${entity.name ?uncap_first}.getId(),${entity.name ?uncap_first}.get${relationShip.otherEntity.name}List().parallelStream().map(it->it.getId()).collect(Collectors.toList()));
        return getSuccessResult(result);
    }
    //解除与${relationShip.otherEntity.name}的关系
    @ApiOperation(value = "✿自动生成✿->解除关联${relationShip.otherEntity.name},只需要传入id即可")
    @PostMapping("/remove${relationShip.otherEntity.name}")
    @ResponseBody
    public ResultWrapper<String> remove${relationShip.otherEntity.name} (@RequestBody ${entity.name} ${entity.name ?uncap_first}){
        String result = ${entity.name ?uncap_first}Service.remove${relationShip.otherEntity.name}(${entity.name ?uncap_first}.getId(),${entity.name ?uncap_first}.get${relationShip.otherEntity.name}List().parallelStream().map(it->it.getId()).collect(Collectors.toList()));
        return getSuccessResult(result);
    }
    <#elseif relationShip.relationType == "ManyToOne" ||relationShip.relationType == "OneToOne">
    @ApiOperation(value = "✿自动生成✿->设置${relationShip.otherEntity.name}关系,只需要传入id即可")
    @PostMapping("/set${relationShip.otherEntity.name}")
    @ResponseBody
    public ResultWrapper<String> set${relationShip.otherEntity.name} (@RequestBody ${entity.name} ${entity.name ?uncap_first}){
        String result = ${entity.name ?uncap_first}Service.set${relationShip.otherEntity.name}(${entity.name ?uncap_first}.getId(),${entity.name ?uncap_first}.get${relationShip.otherEntity.name}().getId());
        return getSuccessResult(result);
    }

    @ApiOperation(value = "✿自动生成✿->取消${relationShip.otherEntity.name}关系,只需要传入id即可")
    @PostMapping("/remove${relationShip.otherEntity.name}")
    @ResponseBody
    public  ResultWrapper<String> remove${relationShip.otherEntity.name} (@RequestBody ${entity.name} ${entity.name ?uncap_first}){
        String result = ${entity.name ?uncap_first}Service.remove${relationShip.otherEntity.name}(${entity.name ?uncap_first}.getId(),${entity.name ?uncap_first}.get${relationShip.otherEntity.name}().getId());
        return getSuccessResult(result);
    }
    </#if>
</#if>
</#list>

}
