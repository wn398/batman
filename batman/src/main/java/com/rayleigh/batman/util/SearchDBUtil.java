package com.rayleigh.batman.util;

import com.rayleigh.batman.service.EntityService;
import com.rayleigh.batman.service.RelationShipService;
import com.rayleigh.core.enums.RelationType;
import com.rayleigh.core.util.SpringContextUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SearchDBUtil {
    //根据id获取实体类名
    public static String getEntityName(String id){
        return ((EntityService) SpringContextUtils.getBean("entityServiceImpl")).findOne(id).getName();
    }

    //通过主对象和副对象id获取它们之间的关系
    public static RelationType getRelationTypeByMainOtherEntityId(String mainEntityId, String otherEntityId){
        return ((RelationShipService)SpringContextUtils.getBean("relationShipServiceImpl")).getByMainEntityAndOtherEntityIds(mainEntityId,otherEntityId).get(0).getRelationType();
    }

    //根据实体id，查询这个实体是否包含相应属性
    public static Boolean isContainField(String entityId,String fieldName){
        List<String> fieldNames =((EntityService) SpringContextUtils.getBean("entityServiceImpl")).findOne(entityId).getFields().parallelStream().map(field -> field.getName()).collect(Collectors.toList());
        if(fieldNames.contains(fieldName)){
            return true;
        }else{
            return false;
        }
    }
}
