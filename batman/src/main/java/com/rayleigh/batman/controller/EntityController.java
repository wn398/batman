package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.Project;
import com.rayleigh.batman.model.SearchMethod;
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
        //验证，传入的字段名称不能包含id,createDate,updateDate,version
        List<String> filedNames = entities.getFields().parallelStream().map(field -> field.getName()).collect(Collectors.toList());
        if(filedNames.size()>0)
        if(filedNames.contains("id")||filedNames.contains("createDate")||filedNames.contains("updateDate")||filedNames.contains("version")){
            return getFailureResultAndInfo(null,"字段名字不能包含id,createDate,updateDate,version,系统已经包含这些字段");
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
            //验证，传入的字段名称不能包含id,createDate,updateDate,version
            List<String> filedNames = entities.getFields().parallelStream().map(field -> field.getName()).collect(Collectors.toList());
            if(filedNames.size()>0)
            if(filedNames.contains("id")||filedNames.contains("createDate")||filedNames.contains("updateDate")||filedNames.contains("version")){
                return getFailureResultAndInfo(null,"字段名字不能包含id,createDate,updateDate,version,系统已经包含这些字段");
            }
            entities.getFields().parallelStream().forEach(field -> {if(StringUtil.isEmpty(field.getValidMessage())){field.setValidMessage(null);}});
            List<String> validationMessages = entities.getFields().parallelStream().map(field -> field.getValidMessage()).collect(Collectors.toList());
            for(String validationMessage:validationMessages){
                if(!StringUtil.isEmpty(validationMessage)) {
                    String[] arrays = validationMessage.split("||");
                    for (int i = 0; i < arrays.length; i++) {
                        if (!arrays[i].contains("@")) {
                            return getFailureResultAndInfo(null,new StringBuilder("验证字段:").append(validationMessage).append("不符合验证规则!").toString());
                        }
                    }
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
