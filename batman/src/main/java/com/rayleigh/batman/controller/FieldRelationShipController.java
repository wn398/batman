package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.FieldRelationShip;
import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.model.RelationShip;
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

/**
 * Created by wangn20 on 2017/6/14.
 */
@Controller
@RequestMapping("/fieldRelationShipCtl")
public class FieldRelationShipController extends BaseController {
    @Autowired
    private FieldRelationShipService fieldRelationShipService;

    @DeleteMapping("/delById")
    @ResponseBody
    public ResultWrapper delById(HttpServletRequest request){
        String id = request.getParameter("id");
        String mainEntityId = request.getParameter("mainEntityId");
        String otherEntityId = request.getParameter("otherEntityId");

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
            return getSuccessResult("success");
        }else{
            return getFailureResultAndInfo(null,"所传id或mainEntityId或otherEntityId为空!");
        }
    }

}
