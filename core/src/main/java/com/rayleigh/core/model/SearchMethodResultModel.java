package com.rayleigh.core.model;

import com.rayleigh.core.enums.OrderBy;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//@ApiModel("查询方法结果")
public class SearchMethodResultModel {

    @ApiModelProperty("字段名")
    private String fieldName;

    @ApiModelProperty("排序优先级")
    private Integer orderByNum = 0;

    @ApiModelProperty("排序类型")
    private OrderBy orderByType;

    @ApiModelProperty("字段所在实体")
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
