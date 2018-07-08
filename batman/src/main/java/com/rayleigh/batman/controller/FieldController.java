package com.rayleigh.batman.controller;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.Field;
import com.rayleigh.batman.service.EntityService;
import com.rayleigh.batman.service.FieldService;
import com.rayleigh.core.controller.BaseController;
import com.rayleigh.core.model.ResultWrapper;
import com.rayleigh.core.service.BaseService;
import com.rayleigh.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Controller
@RequestMapping("/fieldCtl")
public class FieldController extends BaseController {
    @Autowired
    private FieldService fieldService;
    @Autowired
    private EntityService entityService;

    @RequestMapping(value = "/delById")
    @ResponseBody
    public ResultWrapper deleteById(@RequestBody Field field){
        if(null!=field&& !StringUtil.isEmpty(field.getId())){
            try {
                //更新层级时间
                Field field2 = fieldService.findOne(field.getId());
                Entities entities = field2.getEntities();
                entities.setHierachyDate(new Date());
                entityService.update(entities);
                fieldService.deleteById(field.getId());

            }catch (Exception e ){
                getFailureResultAndInfo(field,e.getMessage());
            }
            return getSuccessResult(field);
        }else{
            return getFailureResultAndInfo(field,"传入的id为空!");
        }
    }

    @RequestMapping(value = "/getFieldsByEntity/{id}")
    @ResponseBody
    public ResultWrapper getFieldListByEntity(@PathVariable("id") String entityId){
        if(!StringUtil.isEmpty(entityId)){
            List<Field> list = fieldService.getByEntities(entityId);
            return getSuccessResult(list);
        }else {
            return getFailureResultAndInfo(entityId,"传入的id为空");
        }
    }


}
