package com.rayleigh.core.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体与实体之间的关系类型
 */
@ApiModel("实体之间关系类型")
public enum EntityRelationType {

    @ApiModelProperty("表关联关系")
    TableType("TableType"),

    @ApiModelProperty("字段关联关系")
    FieldType("FieldType"),

    @ApiModelProperty("没有关联关系")
    NoneType("NoneType");
    EntityRelationType(String name){}
}
