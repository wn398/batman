package com.rayleigh.batman.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.*;
import com.rayleigh.batman.uiModel.FieldInfo;
import com.rayleigh.batman.uiModel.SearchConditionResult;
import com.rayleigh.batman.util.BatmanBaseModelUtil;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.enums.LogicOperation;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sqlMethodCtl")
public class SqlMethodController extends BaseController {
    @Autowired
    private SqlMethodService sqlMethodService;
    @Autowired
    private SearchMethodService searchMethodService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private FieldService fieldService;
    @Autowired
    private ModuleService moduleService;


    @PostMapping("/doAdd")
    @ResponseBody
    public ResultWrapper doAdd(@RequestBody String str) {
        SqlMethod sqlMethod = JSON.parseObject(str,SqlMethod.class);
        //todo
        SearchMethod searchMethod = new SearchMethod();
        sqlMethod.setParamListJson(JSON.toJSONString(sqlMethod.getParamList(), SerializerFeature.PrettyFormat));
        sqlMethod.setResultJson(JSON.toJSONString(sqlMethod.getResultList(),SerializerFeature.PrettyFormat));
        sqlMethod.setEntities(entityService.findOne(sqlMethod.getEntities().getId()));
        sqlMethodService.add(sqlMethod);
            //BaseModelUtil.preventMutualRef(searchMethod1, new ArrayList());
            return getSuccessResult("添加成功!");
    }

    @PostMapping("/deleteById/{id}")
    @ResponseBody
    public ResultWrapper deleteById(@PathVariable String id) {
        try {
            sqlMethodService.delete(id);
            return getSuccessResult("success!");
        } catch (Exception e) {
            return getFailureResultAndInfo(id, e.getMessage());
        }
    }

    //到更新页面
    @RequestMapping("/goUpdate")
    public String goUpdate(HttpServletRequest request, Model model) {
        String id = request.getParameter("id");
        String entityId = request.getParameter("entityId");
        SqlMethod sqlMethod = sqlMethodService.findOne(id);
        Entities entities = entityService.findOne(entityId);
        List<FieldInfo> paramList = JSON.parseArray(sqlMethod.getParamListJson(),FieldInfo.class);
        List<FieldInfo> resultList = JSON.parseArray(sqlMethod.getResultJson(),FieldInfo.class);
        sqlMethod.setParamList(paramList);
        sqlMethod.setResultList(resultList);
        model.addAttribute("entity", entities);
        model.addAttribute("method", sqlMethod);
        return "/page/sql-method-update";
    }


    @PostMapping("/doUpdate")
    @ResponseBody
    public ResultWrapper doUpdate(@RequestBody String methodStr){
        SqlMethod sqlMethod = JSON.parseObject(methodStr,SqlMethod.class);
        sqlMethod.setEntities(entityService.findOne(sqlMethod.getEntities().getId()));
        sqlMethod.setParamListJson(JSON.toJSONString(sqlMethod.getParamList(),SerializerFeature.PrettyFormat));
        sqlMethod.setResultJson(JSON.toJSONString(sqlMethod.getResultList(),SerializerFeature.PrettyFormat));

        sqlMethodService.update(sqlMethod);
        return getSuccessResult(sqlMethod);
    }

    @PostMapping("/parseSql")
    @ResponseBody
    public ResultWrapper parseSql(@RequestBody String sql){





        return getSuccessResult(null);

    }
}
