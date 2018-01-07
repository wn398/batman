<#include "CopyRight.ftl">
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

@ApiOperation(value = "保存或更新,包括建立关系,有id则更新,没id则保存")
@PostMapping("/doSaveOrUpdate")
@ResponseBody
public ResultWrapper saveOrUpdate(@RequestBody ${entity.name} ${entity.name ?uncap_first}){

    try {
        ${entity.name} ${entity.name ?uncap_first}Result = ${entity.name ?uncap_first}Service.saveOrUpdate(${entity.name ?uncap_first});
        ${entity.name ?uncap_first}Result = ${entity.name}Util.preventMutualRef(${entity.name ?uncap_first}Result);
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
    if(null != ${entity.name ?uncap_first}.getId()){
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
public ResultWrapper findOneById(@PathVariable("id") ${entityIdType} id){
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
    <#if ((method.isInterface !?exists)||(method.isInterface ?exists &&method.isInterface ?boolean))>
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
    </#if>
</#list>


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
