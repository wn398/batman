package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.EntityService;
import com.rayleigh.batman.service.FieldService;
import com.rayleigh.batman.service.SearchMethodService;
import com.rayleigh.batman.uiModel.SearchConditionResult;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.enums.DataType;
import com.rayleigh.core.enums.LogicOperation;
import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.util.BaseModelUtil;
import com.rayleigh.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/searchMethodCtl")
public class SearchMethodController extends BaseController {
    @Autowired
    private SearchMethodService searchMethodService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private FieldService fieldService;


    @PostMapping("/doAdd")
    @ResponseBody
    public ResultWrapper doAdd(@RequestBody SearchMethod searchMethod) {
        if (null != searchMethod) {
            //对查询条件和查询结果进行过滤
            List<SearchCondition> conditionList = searchMethod.getConditionList();
            List<SearchResult> results = searchMethod.getSearchResults();

            List<SearchCondition> realConditionList = new ArrayList<>();
            List<SearchResult> realResultList = new ArrayList<>();
            //mark主要用于标记前端id,createDate和updateDate三个不在field表里的字段
            for (SearchCondition condition : conditionList) {
                if (null != condition.getField()) {
                    if (!StringUtil.isEmpty(condition.getField().getId())) {
 //                       if (!"mark".equals(condition.getField().getId())) {
                            condition.setField(fieldService.findOne(condition.getField().getId()));
//                        } else {
//                            condition.setField(null);
//                        }
                    }
                    realConditionList.add(condition);
                }
            }
            for (SearchResult result : results) {
                if (null != result.getField()) {
                    if (!StringUtil.isEmpty(result.getField().getId())) {
 //                       if (!"mark".equals(result.getField().getId())) {
                            result.setField(fieldService.findOne(result.getField().getId()));
//                        } else {
//                            result.setField(null);
//                        }
                    }
                    realResultList.add(result);
                }
            }
            Map<Integer,List<SearchCondition>> conditionPriorityMap = realConditionList.parallelStream().collect(Collectors.groupingBy(condition->condition.getPriority()));
            for(Map.Entry<Integer,List<SearchCondition>> entry:conditionPriorityMap.entrySet()){
                Set<LogicOperation> set = entry.getValue().parallelStream().map(condition->condition.getLogicOperation()).collect(Collectors.toSet());
                if(set.size()!=1){
                    return getFailureResultAndInfo(null,"存在同一优先级内不同的逻辑运算符，请检查！");
                }
            }
            searchMethod.setConditionList(realConditionList);
            searchMethod.setSearchResults(realResultList);
            SearchMethod searchMethod1 = (SearchMethod) BaseModelUtil.saveOrUpdateBaseModelObjWithRelationPreProcess(searchMethod);
            searchMethodService.save(searchMethod1);
            //BaseModelUtil.preventMutualRef(searchMethod1, new ArrayList());
            return getSuccessResult("添加成功!");
        } else {
            return getFailureResultAndInfo("", "传入对象为空");
        }
    }

    @PostMapping("/deleteById/{id}")
    @ResponseBody
    public ResultWrapper deleteById(@PathVariable String id) {
        try {
            searchMethodService.delete(id);
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
        SearchMethod searchMethod = searchMethodService.findOne(id);
        Entities entities = entityService.findOne(entityId);
        model.addAttribute("entity", entities);
        model.addAttribute("method", searchMethod);
        // model.addAttribute("conditionNames", searchMethod.getConditionList().parallelStream().map(searchCondition -> searchCondition.getFieldName()).collect(Collectors.toList()));
        List<SearchCondition> conditions = searchMethod.getConditionList();
        List<SearchResult> results = searchMethod.getSearchResults();
        //先设置主对象
        Map<String, SearchConditionResult> mainMap = new LinkedHashMap<>();
        //mainMap初始化
//        mainMap.put(entities.getId() + "_id", new SearchConditionResult(entities.getName(), "id", DataType.String, "mark"));
//        mainMap.put(entities.getId() + "_createDate", new SearchConditionResult(entities.getName(), "createDate", DataType.Date, "mark"));
//        mainMap.put(entities.getId() + "_updateDate", new SearchConditionResult(entities.getName(), "updateDate", DataType.Date, "mark"));
        for (Field field : entities.getFields()) {
            mainMap.put(entities.getId() + "_" + field.getName(), new SearchConditionResult(entities.getName(), field.getName(), field.getDataType(), field.getId()));
        }
        Map<String, SearchCondition> conditionMap = new HashMap<>();
        conditions.parallelStream().forEach(searchCondition -> conditionMap.put(searchCondition.getFieldName(),searchCondition));
        Map<String, SearchResult> searchMethodMap = new HashMap<>();
        results.parallelStream().forEach(searchResult -> searchMethodMap.put(searchResult.getFieldName(),searchResult));
        //先对特殊对象id,createDate,updateDate进行设置
//        String fieldId = entities.getId() + "_id";
//        String fieldCreateDate = entities.getId()+"_createDate";
//        String fieldUpdateDate = entities.getId()+"_updateDate";

//        if(conditionMap.keySet().contains(fieldId)){
//            mainMap.get(fieldId).setSearchCondition(conditionMap.get(fieldId));
//        }else{
//            mainMap.get(fieldId).setSearchCondition(new SearchCondition());
//        }
//
//        if(conditionMap.keySet().contains(fieldCreateDate)){
//            mainMap.get(fieldCreateDate).setSearchCondition(conditionMap.get(fieldCreateDate));
//        }else{
//            mainMap.get(fieldCreateDate).setSearchCondition(new SearchCondition());
//        }
//
//        if(conditionMap.keySet().contains(fieldUpdateDate)){
//            mainMap.get(fieldUpdateDate).setSearchCondition(conditionMap.get(fieldUpdateDate));
//        }else{
//            mainMap.get(fieldUpdateDate).setSearchCondition(new SearchCondition());
//        }
//
//        if(searchMethodMap.keySet().contains(fieldId)){
//            mainMap.get(fieldId).setSearchResult(searchMethodMap.get(fieldId));
//        }else{
//            mainMap.get(fieldId).setSearchResult(new SearchResult());
//        }
//
//        if(searchMethodMap.keySet().contains(fieldCreateDate)){
//            mainMap.get(fieldCreateDate).setSearchResult(searchMethodMap.get(fieldCreateDate));
//        }else{
//            mainMap.get(fieldCreateDate).setSearchResult(new SearchResult());
//        }
//
//        if(searchMethodMap.keySet().contains(fieldUpdateDate)){
//            mainMap.get(fieldUpdateDate).setSearchResult(searchMethodMap.get(fieldUpdateDate));
//        }else{
//            mainMap.get(fieldUpdateDate).setSearchResult(new SearchResult());
//        }

        for (Field field : entities.getFields()) {

                String filedName = entities.getId() + "_" + field.getName();
                if(conditionMap.keySet().contains(filedName)){
                    mainMap.get(filedName).setSearchCondition(conditionMap.get(filedName));
                }else{
                    mainMap.get(filedName).setSearchCondition(new SearchCondition());
                }

                if(searchMethodMap.keySet().contains(filedName)){
                    mainMap.get(filedName).setSearchResult(searchMethodMap.get(filedName));
                }else{
                    mainMap.get(filedName).setSearchResult(new SearchResult());
                }
        }

        Map<String, Map<String, SearchConditionResult>> otherMap = new LinkedHashMap<>();
        //初始化otherMap
        for (RelationShip relationShip : entities.getMainEntityRelationShips()) {
            Entities otherEntity = relationShip.getOtherEntity();
            Map<String, SearchConditionResult> otherEntityMap = new LinkedHashMap<>();
            otherMap.put(otherEntity.getName(), otherEntityMap);

            for (Field field : otherEntity.getFields()) {
                String fieldName = otherEntity.getId() + "_" + field.getName();
                otherEntityMap.put(fieldName, new SearchConditionResult(otherEntity.getName(), field.getName(), field.getDataType(), field.getId()));
            }
        }
        //进行设置
        for (RelationShip relationShip : entities.getMainEntityRelationShips()) {
            Entities otherEntity = relationShip.getOtherEntity();
            Map<String, SearchConditionResult> searchConditionResultMap = otherMap.get(otherEntity.getName());
            for (Field field : otherEntity.getFields()) {
                String fieldName = otherEntity.getId() + "_" + field.getName();
                if(conditionMap.keySet().contains(fieldName)){
                    searchConditionResultMap.get(fieldName).setSearchCondition(conditionMap.get(fieldName));
                }else{
                    searchConditionResultMap.get(fieldName).setSearchCondition(new SearchCondition());
                }

                if(searchMethodMap.keySet().contains(fieldName)){
                    searchConditionResultMap.get(fieldName).setSearchResult(searchMethodMap.get(fieldName));
                }else{
                    searchConditionResultMap.get(fieldName).setSearchResult(new SearchResult());
                }
            }
        }
//////////////////////////////////////////////////////////////////////////////////////////////
        //初始化fieldMap
        Map<String, Map<String, SearchConditionResult>> fieldMap = new LinkedHashMap<>();
        for (FieldRelationShip relationShip : entities.getMainFieldRelationShips()) {
            Entities otherEntity = relationShip.getOtherEntity();
            Map<String, SearchConditionResult> otherEntityMap = new LinkedHashMap<>();
            fieldMap.put(otherEntity.getName(), otherEntityMap);

            for (Field field : otherEntity.getFields()) {
                String fieldName = otherEntity.getId() + "_" + field.getName();
                otherEntityMap.put(fieldName, new SearchConditionResult(otherEntity.getName(), field.getName(), field.getDataType(), field.getId()));
            }
        }

        //进行设置
        for (FieldRelationShip relationShip : entities.getMainFieldRelationShips()) {
            Entities otherEntity = relationShip.getOtherEntity();
            Map<String, SearchConditionResult> searchConditionResultMap = fieldMap.get(otherEntity.getName());

            for (Field field : otherEntity.getFields()) {
                String fieldName = otherEntity.getId() + "_" + field.getName();
                if(conditionMap.keySet().contains(fieldName)){
                    searchConditionResultMap.get(fieldName).setSearchCondition(conditionMap.get(fieldName));
                }else{
                    searchConditionResultMap.get(fieldName).setSearchCondition(new SearchCondition());
                }

                if(searchMethodMap.keySet().contains(fieldName)){
                    searchConditionResultMap.get(fieldName).setSearchResult(searchMethodMap.get(fieldName));
                }else{
                    searchConditionResultMap.get(fieldName).setSearchResult(new SearchResult());
                }
            }
        }
        model.addAttribute("fieldMap",fieldMap);
        model.addAttribute("mainMap", mainMap);
        model.addAttribute("otherMap", otherMap);


        return "/page/method-update";
    }


    @PostMapping("/doUpdate")
    @ResponseBody
    public ResultWrapper doUpdate(@RequestBody SearchMethod searchMethod){
        if (null != searchMethod) {
            //对查询条件和查询结果进行过滤
            List<SearchCondition> conditionList = searchMethod.getConditionList();
            List<SearchResult> results = searchMethod.getSearchResults();

            List<SearchCondition> realConditionList = new ArrayList<>();
            List<SearchResult> realResultList = new ArrayList<>();
            //mark主要用于标记前端id,createDate和updateDate三个不在field表里的字段
            for (SearchCondition condition : conditionList) {
                if (null != condition.getField()) {
                    if (!StringUtil.isEmpty(condition.getField().getId())) {
                        //if (!"mark".equals(condition.getField().getId())) {
                            condition.setField(fieldService.findOne(condition.getField().getId()));
//                        } else {
//                            condition.setField(null);
//                        }
                    }
                    realConditionList.add(condition);
                }
            }
            for (SearchResult result : results) {
                if (null != result.getField()) {
                    if (!StringUtil.isEmpty(result.getField().getId())) {
                     //   if (!"mark".equals(result.getField().getId())) {
                            result.setField(fieldService.findOne(result.getField().getId()));
//                        } else {
//                            result.setField(null);
//                        }
                    }
                    realResultList.add(result);
                }
            }

            Map<Integer,List<SearchCondition>> conditionPriorityMap = realConditionList.parallelStream().collect(Collectors.groupingBy(condition->condition.getPriority()));
            for(Map.Entry<Integer,List<SearchCondition>> entry:conditionPriorityMap.entrySet()){
                Set<LogicOperation> set = entry.getValue().parallelStream().map(condition->condition.getLogicOperation()).collect(Collectors.toSet());
                if(set.size()!=1){
                    return getFailureResultAndInfo(searchMethod,"存在同一优先级内不同的逻辑运算符，请检查！");
                }
            }

            searchMethod.setConditionList(realConditionList);
            searchMethod.setSearchResults(realResultList);
            SearchMethod searchMethod1 = (SearchMethod) BaseModelUtil.saveOrUpdateBaseModelObjWithRelationPreProcess(searchMethod);
            searchMethodService.save(searchMethod1);
            BaseModelUtil.preventMutualRef(searchMethod1, new ArrayList());
            return getSuccessResult(searchMethod1);
        } else {
            return getFailureResultAndInfo("", "传入对象为空");
        }
    }
}
