package com.rayleigh.core.model;

import com.rayleigh.core.enums.OrderBy;

public class SearchMethodResultModel {
    //字段名
    private String fieldName;
    //排序优先级
    private Integer orderByNum = 0;
    //排序类型
    private OrderBy orderByType;
    //字段所在实体名字
    private String entityName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getOrderByNum() {
        return orderByNum;
    }

    public void setOrderByNum(Integer orderByNum) {
        this.orderByNum = orderByNum;
    }

    public OrderBy getOrderByType() {
        return orderByType;
    }

    public void setOrderByType(OrderBy orderByType) {
        this.orderByType = orderByType;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

}
