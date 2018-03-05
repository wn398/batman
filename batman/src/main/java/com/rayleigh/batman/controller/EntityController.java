package com.rayleigh.batman.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.EntityService;
import com.rayleigh.batman.service.ModuleService;
import com.rayleigh.batman.service.ProjectService;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.enums.ResultStatus;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.service.BaseService;
import com.rayleigh.core.util.BaseModelUtil;
import com.rayleigh.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Controller
@RequestMapping("/entitiesCtl")
public class EntityController extends BaseController{
    @Autowired
    private EntityService entityService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ModuleService moduleService;

    @PostMapping(value = "/doAdd")
    @ResponseBody
    public ResultWrapper doAdd(@Valid @RequestBody Entities entities){
        if(!StringUtil.isCapFirst(entities.getName())){
            return getFailureResultAndInfo(null,"实体名称首字母不能小写!");
        }
        //添加对字段名字和描述不能为空的验证
        for(Field field:entities.getFields()){
            if(StringUtil.isEmpty(field.getName())||StringUtil.isEmpty(field.getDescription())){
                return getFailureResultAndInfo(field,new StringBuilder("字段名或描述不能为空!").toString());
            }
            if(!StringUtil.isUnCapFirst(field.getName())){
                return getFailureResultAndInfo(field,new StringBuilder("字段名首字母不能大写:").append(field.getName()).toString());
            }
        }

        entities.getFields().parallelStream().forEach(field -> {if(StringUtil.isEmpty(field.getValidMessage())){field.setValidMessage(null);}});
        List<String> validationMessages = entities.getFields().parallelStream().map(field -> field.getValidMessage()).collect(Collectors.toList());
        for(String validationMessage:validationMessages){
                if(null!=validationMessage) {
                    String[] arrays = validationMessage.split("||");
                    for (int i = 0; i < arrays.length; i++) {
                        if (!arrays[i].contains("@")) {
                            return getFailureResultAndInfo(null, new StringBuilder("验证字段:").append(validationMessage).append("不符合验证规则!").toString());
                        }
                    }
                }

        }

        Entities resultEntities = (Entities) BaseModelUtil.saveOrUpdateBaseModelObjWithRelationPreProcess(entities);

        Entities entities1 = entityService.save(resultEntities);
        BaseModelUtil.preventMutualRef(entities1,new ArrayList());

        return getSuccessResult(entities1);
    }

    @PostMapping(value = "/partUpdate")
    @ResponseBody
    public ResultWrapper partUpdate(@RequestBody Entities entities){

        if(null!=entities&& !StringUtil.isEmpty(entities.getId())) {

            if(!StringUtil.isCapFirst(entities.getName())){
                return getFailureResultAndInfo(null,"实体名称首字母不能小写!");
            }
            //添加对字段名字和描述不能为空的验证
            for(Field field:entities.getFields()){
                if(StringUtil.isEmpty(field.getName())||StringUtil.isEmpty(field.getDescription())){
                    return getFailureResultAndInfo(field,new StringBuilder("字段名或描述不能为空!-").append(JSON.toJSON(field)).toString());
                }
                if(!StringUtil.isUnCapFirst(field.getName())){
                    return getFailureResultAndInfo(field,new StringBuilder("字段名首字母不能大写:").append(field.getName()).toString());
                }
            }
            //检测字段名是否重复
            Map<String,Long> map = entities.getFields().parallelStream().map(field -> field.getName()).collect(Collectors.groupingBy(it->it,Collectors.counting()));
            if(map.size() !=entities.getFields().size()){
                List<String> list2 = map.entrySet().parallelStream().filter(it->it.getValue()>1).map(it->it.getKey()).collect(Collectors.toList());
                return getFailureResultAndInfo(list2, new StringBuilder("属性名重复:").append(list2.parallelStream().collect(Collectors.joining(","))).toString());
            }
            entities.getFields().parallelStream().forEach(field -> {if(StringUtil.isEmpty(field.getValidMessage())){field.setValidMessage(null);}});
            List<String> validationMessages = entities.getFields().parallelStream().map(field -> field.getValidMessage()).filter(message->!StringUtil.isEmpty(message)).collect(Collectors.toList());
            for(String validationMessage:validationMessages){
                if(!StringUtil.isEmpty(validationMessage)) {
                    String[] arrays = validationMessage.split("[||]");
                    for (int i = 0; i < arrays.length; i++) {
                        if (!StringUtil.isEmpty(arrays[i])&&!arrays[i].contains("@")) {
                            return getFailureResultAndInfo(null,new StringBuilder("验证字段:").append(validationMessage).append("不符合验证规则!").toString());
                        }
                    }
                }
            }
            //如果主键类型变了，要查看相关联的实体类型是否一致
            Entities dbEntities = entityService.findOne(entities.getId());
            if(dbEntities.getPrimaryKeyType()!=entities.getPrimaryKeyType()){
                if(dbEntities.getMainEntityRelationShips().size()>0){
                    return getFailureResultAndInfo(null,"不能修改主键类型，存在关联外键!");
                }
            }

            Entities entities1 = entityService.partUpdate(entities);
            //entities1 = preventCirculation(entities1);
            BaseModelUtil.preventMutualRef(entities1,new ArrayList());
            return getSuccessResult(entities1);
        }else{
            return getFailureResultAndInfo(entities,"id不能空!");
        }
    }


    @RequestMapping("/goUpdate")
    public String goUpdate(HttpServletRequest request, Model model){
        String id = request.getParameter("id");
        if(!StringUtil.isEmpty(id)) {
            Entities entities = entityService.findOne(id);
            model.addAttribute("entities", entities);
            return "/page/entities-update";
        }else{
            return "/error";
        }
    }


    @DeleteMapping(value = "/doDelete")
    @ResponseBody
    public ResultWrapper delete(@RequestBody Entities entities){
        if(null!=entities&&!StringUtil.isEmpty(entities.getId())) {
            try {
                entityService.deleteOne(entities);
                return getSuccessResult(ResultStatus.SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                return getFailureResult(ResultStatus.FAILURE);
            }
        }else{
            return getFailureResultAndInfo(entities,"请传入id");
        }
    }
    //去一个实体类的方法列表页面
    @RequestMapping("/showEntityMethod/{id}")
    public String showEntityMethod(Model model,@PathVariable("id") String entityId){
        Entities entities = entityService.findOne(entityId);
        model.addAttribute("entity",entities);
        return "/page/method-list";
    }

    @PostMapping("/getEntityMethodData/{id}")
    @ResponseBody
    public ResultWrapper showEntityMethodData(@PathVariable("id") String entityId){
        Entities entities = entityService.findOne(entityId);
        List<SearchMethod> methods = entities.getMethods();
        //设置默认值
        methods.parallelStream().forEach(method -> {
        if(null ==method.getIsReturnObject()){
            method.setIsReturnObject(false);
        if(null ==method.getIsInterface()){
            method.setIsInterface(true);
        }
        }});
        methods.parallelStream().forEach(method->{method.setConditionList(null);method.setSearchResults(null);
            method.setEntities(null);
        });

        return getSuccessResult(methods);
    }

    //方法增加页面
    @RequestMapping("/goAddMethod/{id}")
    public String goAddMethod(Model model,@PathVariable("id") String entityId){
        Entities entities = entityService.findOne(entityId);
        model.addAttribute("entity",entities);
        return "/page/method-add";
    }
    //防止循环检测
    private Entities preventCirculation(Entities entities){
        if(null!=entities&&null!=entities.getModule()){
            entities.getModule().setEntities(null);
        }
        if(null!=entities&&entities.getFields().size()>0){
            entities.getFields().parallelStream().forEach(field -> field.setEntities(null));
        }
        return entities;
    }

}
