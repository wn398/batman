package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.*;
import com.rayleigh.batman.service.*;
import com.rayleigh.batman.uiModel.ModelTableFieldRalationShip;
import com.rayleigh.core.exception.NotBaseModelException;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.enums.RelationType;
import com.rayleigh.core.model.BaseModel;
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
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wangn20 on 2017/6/14.
 */
@Controller
@RequestMapping("/relationShipCtl")
public class RelationShipController extends BaseController {
    @Autowired
    private EntityService entityService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private RelationShipService relationShipService;
    @Autowired
    private FieldRelationShipService fieldRelationShipService;
    @Autowired
    private FieldService fieldService;

    @RequestMapping("/goList")
    public String goEntitiesRelationShipList(Model model, HttpServletRequest request){
        String entitiesId = request.getParameter("entitiesId");
        String moduleId = request.getParameter("moduleId");
        Entities entities = entityService.findOne(entitiesId);
        Module module = moduleService.findOne(moduleId);
        model.addAttribute("mainEntites",entities);
        model.addAttribute("otherEntitesList",module.getEntities());
        List<RelationShip> relationShipList = relationShipService.getByMainEntity(entities);
        model.addAttribute("relationShipList",relationShipList);
        List<FieldRelationShip> fieldRelationShipList = fieldRelationShipService.getByMainEntity(entities);
        model.addAttribute("fieldRelationShipList",fieldRelationShipList);
        return "/page/relationShip-list";
    }

    @RequestMapping("/doSaveOrUpdate")
    @ResponseBody
    public ResultWrapper doSaveOrUpdate(@RequestBody Map<String,List<ModelTableFieldRalationShip>> relationShipMap, HttpServletRequest request){
        if(null==relationShipMap||relationShipMap.size()==0){
            return getFailureResultAndInfo(relationShipMap,"请传入数据！！！");
        }
        //处理表关联
        List<ModelTableFieldRalationShip> list = relationShipMap.get("relationShip");
        List<RelationShip> list3=null;
        if(null!=list) {
            for (ModelTableFieldRalationShip relationShip : list) {
                Entities mainEntity = entityService.findOne(relationShip.getMainEntity().getId());
                Entities otherEntity = entityService.findOne(relationShip.getOtherEntity().getId());
                if (!mainEntity.getPrimaryKeyType().equals(otherEntity.getPrimaryKeyType())) {
                    return getFailureResultAndInfo(relationShipMap, relationShip.getMainEntity().getName() + "的主键类型与" + relationShip.getOtherEntity().getName() + "的主键类型不一样，不能建立外键关联!");
                }
            }
            boolean isRepeated = isOtherEntityRepeated(list);
            if (isRepeated) {
                return getFailureResultAndInfo(relationShipMap, "所建立关系对象有重复，请检查!");
            }
            List<RelationShip> twoSideList = new ArrayList<>();

            list.stream().forEach(relationShip -> {
                RelationShip oppositeRelationShip = constructOppositeRelationShip(relationShip);
                if (null != oppositeRelationShip) {
                    twoSideList.add(oppositeRelationShip);
                    twoSideList.add(getRelationShip(relationShip));
                } else {
                    twoSideList.add(getRelationShip(relationShip));
                }
            });
            try {
                list3 = (List<RelationShip>) BaseModelUtil.saveOrUpdateBaseModelObjWithRelationPreProcess(twoSideList);
            } catch (NotBaseModelException e) {
                e.printStackTrace();
                return getFailureResultAndInfo(relationShipMap, e.getMessage());
            }
        }

        //处理字段关联
        List<ModelTableFieldRalationShip> fieldRelationShips = relationShipMap.get("fieldRelationShip");
        List<FieldRelationShip> list4=null;
        if(null!=fieldRelationShips) {
            for (ModelTableFieldRalationShip fieldRelationShip : fieldRelationShips) {

                Field mainField = fieldService.findOne(fieldRelationShip.getMainField().getId());
                Field otherField = fieldService.findOne(fieldRelationShip.getOtherField().getId());
                if (!mainField.getDataType().toString().equals(otherField.getDataType().toString())) {
                    Entities mainEntity = entityService.findOne(fieldRelationShip.getMainEntity().getId());
                    Entities otherEntity = entityService.findOne(fieldRelationShip.getOtherEntity().getId());
                    StringBuilder sb = new StringBuilder("实体:").append(mainEntity.getDescription()).append("(").append(mainEntity.getName()).append(")的字段:").append(mainField.getDescription()).append("(")
                            .append(mainField.getName()).append(")与实体:").append(otherEntity.getDescription()).append("(").append(otherEntity.getName()).append(")的字段:").append(otherField.getDescription())
                            .append("(").append(otherField.getName()).append(")数据类型不一致！不能关联!");
                    return getFailureResultAndInfo(relationShipMap, sb.toString());
                }
            }
            List<FieldRelationShip> twoSideFieldList = new ArrayList<>();
            fieldRelationShips.stream().forEach(fieldRelationShip -> {
                FieldRelationShip oppositeFiledRelationShip = constructOppositeFieldRelationShip(fieldRelationShip);
                if (null != oppositeFiledRelationShip) {
                    twoSideFieldList.add(oppositeFiledRelationShip);
                    twoSideFieldList.add(getFieldRelationShip(fieldRelationShip));
                } else {
                    twoSideFieldList.add(getFieldRelationShip(fieldRelationShip));
                }
            });

            try {
                list4 = (List<FieldRelationShip>) BaseModelUtil.saveOrUpdateBaseModelObjWithRelationPreProcess(twoSideFieldList);
            } catch (NotBaseModelException e) {
                e.printStackTrace();
                return getFailureResultAndInfo(relationShipMap, e.getMessage());
            }
        }


        relationShipService.saveRelationShipListAndFieldRelationShip(list3,list4);

        //增加有关联关系，同步更新到对应的实体上时间，下次生成代码时可以做相应的修改
        Map<String,Entities> needEntity = new HashMap<>();
        if(null!=list3&&list3.size()>0) {
            list3.parallelStream().forEach(it -> {
                needEntity.put(it.getMainEntity().getId(),it.getMainEntity());
                needEntity.put(it.getOtherEntity().getId(),it.getOtherEntity());
            });
        }
        //list4是字段关联
        if(null!=list4&&list4.size()>0) {
            list4.parallelStream().forEach(it -> {
                needEntity.put(it.getMainEntity().getId(),it.getMainEntity());
                needEntity.put(it.getOtherEntity().getId(),it.getOtherEntity());
            });
        }
        if(null!=needEntity&&needEntity.size()>0) {
            needEntity.values().parallelStream().forEach(it -> {
                Entities entities = entityService.findOne(it.getId());
                entities.setHierachyDate(new Date());
                entityService.update(entities);
            });
        }
        return getSuccessResult("保存成功!");
    }



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
            List<RelationShip> oppositeRelationShipList =relationShipService.getByMainEntityAndOtherEntityIds(otherEntityId,mainEntityId);
            //如果两个id相同，会一次性删除两个关系
            if(!mainEntityId.equals(otherEntityId)) {
                for (RelationShip relationShip : oppositeRelationShipList) {
                    relationShipService.delete(relationShip.getId());
                }
                //删除本方的关系
                relationShipService.delete(id);
            }else{
                for (RelationShip relationShip : oppositeRelationShipList) {
                        relationShipService.delete(relationShip.getId());
                }
            }
            Entities entities = entityService.findOne(mainEntityId);
            entities.setHierachyDate(new Date());
            Entities otherEntity = entityService.findOne(otherEntityId);
            entities.setHierachyDate(new Date());
            entityService.update(entities);
            entityService.update(otherEntity);
            
            return getSuccessResult("success");
        }else{
            return getFailureResultAndInfo(null,"所传id或mainEntityId或otherEntityId为空!");
        }
    }

    private RelationShip getRelationShip(ModelTableFieldRalationShip modelTableFieldRalationShip){
        RelationShip relationShip = new RelationShip();
        relationShip.setIsCascadeDelete(modelTableFieldRalationShip.getIsCascadeDelete());
        relationShip.setIsLazyFetch(modelTableFieldRalationShip.getIsLazyFetch());
        relationShip.setMainEntity(modelTableFieldRalationShip.getMainEntity());
        relationShip.setOtherEntity(modelTableFieldRalationShip.getOtherEntity());
        relationShip.setRelationType(modelTableFieldRalationShip.getRelationType());
        relationShip.setCreateDate(modelTableFieldRalationShip.getCreateDate());
        relationShip.setId(modelTableFieldRalationShip.getId());
        relationShip.setUpdateDate(modelTableFieldRalationShip.getUpdateDate());
        relationShip.setVersion(modelTableFieldRalationShip.getVersion());
        return relationShip;
    }

    private FieldRelationShip getFieldRelationShip(ModelTableFieldRalationShip modelTableFieldRalationShip){
        FieldRelationShip fieldRelationShip = new FieldRelationShip();
        fieldRelationShip.setOtherField(modelTableFieldRalationShip.getOtherField());
        fieldRelationShip.setMainField(modelTableFieldRalationShip.getMainField());
        fieldRelationShip.setOtherEntity(modelTableFieldRalationShip.getOtherEntity());
        fieldRelationShip.setMainEntity(modelTableFieldRalationShip.getMainEntity());
        fieldRelationShip.setRelationType(modelTableFieldRalationShip.getRelationType());
        fieldRelationShip.setCreateDate(modelTableFieldRalationShip.getCreateDate());
        fieldRelationShip.setId(modelTableFieldRalationShip.getId());
        fieldRelationShip.setUpdateDate(modelTableFieldRalationShip.getUpdateDate());
        fieldRelationShip.setVersion(modelTableFieldRalationShip.getVersion());
        return fieldRelationShip;
    }
    //检查与之建立关系的对象，防止重复
    private Boolean isOtherEntityRepeated(List<ModelTableFieldRalationShip> list){
        List<ModelTableFieldRalationShip> list2 = new ArrayList<>();
        //去掉自关联时的自己关联自己
        for(ModelTableFieldRalationShip relationShip:list){
            if(!relationShip.getMainEntity().getId().equals(relationShip.getOtherEntity().getId())){
                list2.add(relationShip);
            }
        }
        Set<String> set = new HashSet<>();
        list2.parallelStream().forEach(relationShip -> set.add(relationShip.getOtherEntity().getId()));
        if(set.size()==list2.size()){
            return false;
        }else{
            return true;
        }
    }

    //构造一个双向映射的另一个映射关系,如果不存在id则新建一个相反的返回，如果存在id，并且作了修改关系，则把另一个关系对应修改了返回，以作持久化
    private RelationShip constructOppositeRelationShip(ModelTableFieldRalationShip relationShip){
        if(StringUtil.isEmpty(relationShip.getId())){
            //如果是一对一，并且双方为同一个实体类，则不作反向增加关系
            if(relationShip.getRelationType()==RelationType.OneToOne){
                //获取类名
                String mainEntityName = entityService.findOne(relationShip.getMainEntity().getId()).getName();
                String otherEntityName = entityService.findOne(relationShip.getOtherEntity().getId()).getName();
                if(mainEntityName.equals(otherEntityName)) {
                    return null;
                }
            }
            RelationShip oppositeRelationShip = new RelationShip();
            oppositeRelationShip.setMainEntity(relationShip.getOtherEntity());
            oppositeRelationShip.setOtherEntity(relationShip.getMainEntity());
            RelationType type = relationShip.getRelationType();
            if(type==RelationType.OneToMany){
                oppositeRelationShip.setRelationType(RelationType.ManyToOne);
            }else if(type ==RelationType.ManyToOne){
                oppositeRelationShip.setRelationType(RelationType.OneToMany);
            }else{
                oppositeRelationShip.setRelationType(relationShip.getRelationType());
            }
            //默认设置
            oppositeRelationShip.setIsCascadeDelete(false);
           // oppositeRelationShip.setIsCascadeUpdate(false);
            oppositeRelationShip.setIsLazyFetch(true);
            return oppositeRelationShip;
        }else{
            //如果存在id，这一方的关系改变了，就需要改变另一方的关系变成对应的关联关系，先查出对方的关系
            List<RelationShip> otherRelationShips = relationShipService.getByMainEntityAndOtherEntityIds(relationShip.getOtherEntity().getId(),relationShip.getMainEntity().getId());
            RelationShip oppositeRelationShip = null;
            if(null!=otherRelationShips && otherRelationShips.size()>0) {
                oppositeRelationShip = otherRelationShips.get(0);
            }
            if(null!=oppositeRelationShip){
                RelationType type = relationShip.getRelationType();
                RelationType otherType = oppositeRelationShip.getRelationType();
                if(type ==RelationType.OneToMany){
                    if(otherType !=RelationType.ManyToOne) {
                        oppositeRelationShip.setRelationType(RelationType.ManyToOne);
                        return oppositeRelationShip;
                    }
                }else if(type ==RelationType.ManyToOne){
                    if(otherType !=RelationType.OneToMany){
                        oppositeRelationShip.setRelationType(RelationType.OneToMany);
                        return oppositeRelationShip;
                    }
                }else if(type == RelationType.OneToOne){
                    if(otherType !=RelationType.OneToOne){
                        oppositeRelationShip.setRelationType(RelationType.OneToOne);
                        return oppositeRelationShip;
                    }
                }else if(type == RelationType.ManyToMany){
                    if(otherType !=RelationType.ManyToMany){
                        oppositeRelationShip.setRelationType(RelationType.ManyToMany);
                        return oppositeRelationShip;
                    }
                }
            }
            return null;
        }
    }

    //构造一个字段双向映射的另一个映射关系,如果不存在id则新建一个相反的返回，如果存在id，并且作了修改关系，则把另一个关系对应修改了返回，以作持久化
    private FieldRelationShip constructOppositeFieldRelationShip(ModelTableFieldRalationShip fieldRelationShip){
        if(StringUtil.isEmpty(fieldRelationShip.getId())){
            //如果是一对一，并且双方为同一个实体类，则不作反向增加关系
            if(fieldRelationShip.getRelationType()==RelationType.OneToOne){
                //获取类名
                String mainEntityName = entityService.findOne(fieldRelationShip.getMainEntity().getId()).getName();
                String otherEntityName = entityService.findOne(fieldRelationShip.getOtherEntity().getId()).getName();
                if(mainEntityName.equals(otherEntityName)) {
                    return null;
                }
            }
            FieldRelationShip oppositeRelationShip = new FieldRelationShip();
            oppositeRelationShip.setMainEntity(fieldRelationShip.getOtherEntity());
            oppositeRelationShip.setMainField(fieldRelationShip.getOtherField());
            oppositeRelationShip.setOtherEntity(fieldRelationShip.getMainEntity());
            oppositeRelationShip.setOtherField(fieldRelationShip.getMainField());
            RelationType type = fieldRelationShip.getRelationType();
            if(type==RelationType.OneToMany){
                oppositeRelationShip.setRelationType(RelationType.ManyToOne);
            }else if(type ==RelationType.ManyToOne){
                oppositeRelationShip.setRelationType(RelationType.OneToMany);
            }else{
                oppositeRelationShip.setRelationType(fieldRelationShip.getRelationType());
            }
            return oppositeRelationShip;
        }else{
            //如果存在id，这一方的关系改变了，就需要改变另一方的关系变成对应的关联关系，先查出对方的关系
            List<FieldRelationShip> otherRelationShips = fieldRelationShipService.getByMainEntityAndOtherEntityIds(fieldRelationShip.getOtherEntity().getId(),fieldRelationShip.getMainEntity().getId());
            FieldRelationShip oppositeRelationShip = null;
            if(null!=otherRelationShips && otherRelationShips.size()>0) {
                oppositeRelationShip = otherRelationShips.get(0);
            }
            if(null!=oppositeRelationShip){
                RelationType type = fieldRelationShip.getRelationType();
                RelationType otherType = oppositeRelationShip.getRelationType();
                if(type ==RelationType.OneToMany){
                    if(otherType !=RelationType.ManyToOne) {
                        oppositeRelationShip.setRelationType(RelationType.ManyToOne);
                        return oppositeRelationShip;
                    }
                }else if(type ==RelationType.ManyToOne){
                    if(otherType !=RelationType.OneToMany){
                        oppositeRelationShip.setRelationType(RelationType.OneToMany);
                        return oppositeRelationShip;
                    }
                }else if(type == RelationType.OneToOne){
                    if(otherType !=RelationType.OneToOne){
                        oppositeRelationShip.setRelationType(RelationType.OneToOne);
                        return oppositeRelationShip;
                    }
                }else if(type == RelationType.ManyToMany){
                    if(otherType !=RelationType.ManyToMany){
                        oppositeRelationShip.setRelationType(RelationType.ManyToMany);
                        return oppositeRelationShip;
                    }
                }
            }
            return null;
        }
    }

}
