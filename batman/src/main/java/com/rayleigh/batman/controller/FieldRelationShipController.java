package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.EntityService;
import com.rayleigh.batman.service.FieldRelationShipService;
import com.rayleigh.batman.service.ModuleService;
import com.rayleigh.batman.service.RelationShipService;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.enums.RelationType;
import com.rayleigh.core.exception.NotBaseModelException;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.util.BaseModelUtil;
import com.rayleigh.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wangn20 on 2017/6/14.
 */
@Controller
@RequestMapping("/fieldRelationShipCtl")
public class FieldRelationShipController extends BaseController {
    @Autowired
    private FieldRelationShipService fieldRelationShipService;
    @Autowired
    private EntityService entityService;

    @DeleteMapping("/delById")
    @ResponseBody
    public ResultWrapper delById(HttpServletRequest request){
        String id = request.getParameter("id");
        String mainEntityId = request.getParameter("mainEntityId");
        String otherEntityId = request.getParameter("otherEntityId");
        List<String> entityIdList = new ArrayList<>();
        entityIdList.add(mainEntityId);
        entityIdList.add(otherEntityId);
        //查询有没有方法关联着这两个实体
        List<SearchMethod> list = entityService.findOne(mainEntityId).getMethods();
        for(SearchMethod searchMethod: list){
            List<SearchResult> searchResultList = searchMethod.getSearchResults();
            if(null!=searchResultList && searchResultList.size()>0 && searchResultList.parallelStream().map(searchResult -> searchResult.getField().getEntities().getId()).collect(Collectors.toSet()).containsAll(entityIdList)){
                return getFailureResultAndInfo(null,new StringBuilder("实体 ").append(entityService.findOne(mainEntityId).getName()).append("的方法 ").append(searchMethod.getMethodName()).append("结果引用了此关系，请先删除此方法!").toString());
            }
            List<SearchCondition> searchConditionList = searchMethod.getConditionList();
            if(null!=searchConditionList && searchConditionList.size()>0 && searchConditionList.parallelStream().map(condition -> condition.getField().getEntities().getId()).collect(Collectors.toSet()).containsAll(entityIdList)){
                return getFailureResultAndInfo(null,new StringBuilder("实体 ").append(entityService.findOne(mainEntityId).getName()).append("的方法 ").append(searchMethod.getMethodName()).append("条件引用了此关系，请先删除此方法!").toString());
            }

        }

        List<SearchMethod> otherList = entityService.findOne(otherEntityId).getMethods();
        for(SearchMethod searchMethod: otherList){
            List<SearchResult> searchResultList = searchMethod.getSearchResults();
            if(null!=searchResultList && searchResultList.size()>0 && searchResultList.parallelStream().map(searchResult -> searchResult.getField().getEntities().getId()).collect(Collectors.toSet()).containsAll(entityIdList)){
                return getFailureResultAndInfo(null,new StringBuilder("实体 ").append(entityService.findOne(mainEntityId).getName()).append("的方法 ").append(searchMethod.getMethodName()).append("结果引用了此关系，请先删除此方法!").toString());
            }
            List<SearchCondition> searchConditionList = searchMethod.getConditionList();
            if(null !=searchConditionList && searchConditionList.size()>0 && searchConditionList.parallelStream().map(condition -> condition.getField().getEntities().getId()).collect(Collectors.toSet()).containsAll(entityIdList)){
                return getFailureResultAndInfo(null,new StringBuilder("实体 ").append(entityService.findOne(mainEntityId).getName()).append("的方法 ").append(searchMethod.getMethodName()).append("条件引用了此关系，请先删除此方法!").toString());
            }

        }

        if(!StringUtil.isEmpty(id)&&!StringUtil.isEmpty(mainEntityId)&&!StringUtil.isEmpty(otherEntityId)){
            //获取另一方的关系删除
            List<FieldRelationShip> oppositeRelationShipList =fieldRelationShipService.getByMainEntityAndOtherEntityIds(otherEntityId,mainEntityId);
            //如果两个id相同，会一次性删除两个关系
            if(!mainEntityId.equals(otherEntityId)) {
                for (FieldRelationShip relationShip : oppositeRelationShipList) {
                    fieldRelationShipService.delete(relationShip.getId());
                }
                //删除本方的关系
                fieldRelationShipService.delete(id);
            }else{
                for (FieldRelationShip relationShip : oppositeRelationShipList) {
                    fieldRelationShipService.delete(relationShip.getId());
                }
            }
            //更新hierachyDate
            Entities mainEntity = entityService.findOne(mainEntityId);
            mainEntity.setHierachyDate(new Date());
            entityService.update(mainEntity);

            Entities otherEntity = entityService.findOne(otherEntityId);
            otherEntity.setHierachyDate(new Date());
            entityService.update(otherEntity);

            return getSuccessResult("success");
        }else{
            return getFailureResultAndInfo(null,"所传id或mainEntityId或otherEntityId为空!");
        }
    }

}
