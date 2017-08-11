package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.model.RelationShip;
import com.rayleigh.batman.service.EntityService;
import com.rayleigh.batman.service.ModuleService;
import com.rayleigh.batman.service.RelationShipService;
import com.rayleigh.core.Exception.NotBaseModelException;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.enums.RelationType;
import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.service.BaseService;
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
        return "/page/relationShip-list";
    }

    @RequestMapping("/doSaveOrUpdate")
    @ResponseBody
    public ResultWrapper doSaveOrUpdate(@RequestBody Map<String,List<RelationShip>> relationShipMap){
        if(null==relationShipMap||relationShipMap.size()==0){
            return getFailureResultAndInfo(relationShipMap,"请传入数据！！！");
        }
        List<RelationShip> list = relationShipMap.get("relationShip");
        boolean isRepeated = isOtherEntityRepeated(list);
        if(isRepeated){
            return getFailureResultAndInfo(relationShipMap,"所建立关系对象有重复，请检查!");
        }
        List<RelationShip> twoSideList = new ArrayList<>();

        list.stream().forEach(relationShip -> {
            RelationShip oppositeRelationShip = constructOppositeRelationShip(relationShip);
            if(null!=oppositeRelationShip){
                twoSideList.add(oppositeRelationShip);
                twoSideList.add(relationShip);
            }else{
                twoSideList.add(relationShip);
            }
        });

        List<RelationShip> list3;
        try {
            list3 = (List<RelationShip>)BaseModelUtil.saveOrUpdateBaseModelObjWithRelationPreProcess(twoSideList);
        } catch (NotBaseModelException e) {
            e.printStackTrace();
            return getFailureResultAndInfo(relationShipMap,e.getMessage());
        }
        if(null!=list3) {
            relationShipService.save(list3);
        }
        return getSuccessResult(list3);
    }

    @DeleteMapping("/delById")
    @ResponseBody
    public ResultWrapper delById(HttpServletRequest request){
        String id = request.getParameter("id");
        String mainEntityId = request.getParameter("mainEntityId");
        String otherEntityId = request.getParameter("otherEntityId");

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
            return getSuccessResult("success");
        }else{
            return getFailureResultAndInfo(null,"所传id或mainEntityId或otherEntityId为空!");
        }
    }
    //检查与之建立关系的对象，防止重复
    private Boolean isOtherEntityRepeated(List<RelationShip> list){
        Set<String> set = new HashSet<>();
        list.parallelStream().forEach(relationShip -> set.add(relationShip.getOtherEntity().getId()));
        if(set.size()==list.size()){
            return false;
        }else{
            return true;
        }
    }

    //构造一个双向映射的另一个映射关系,如果不存在id则新建，如果存在id，则是更新不作改变，返回null
    private RelationShip constructOppositeRelationShip(RelationShip relationShip){
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
            return null;
        }
    }

}
