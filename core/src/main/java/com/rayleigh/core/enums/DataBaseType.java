package com.rayleigh.core.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("数据库枚举")
public enum DataBaseType {
    @ApiModelProperty("MySQL")
    MySQL("MySQL"),
    @ApiModelProperty("PostgreSql")
    PostgreSql("PostgreSql"),
    @ApiModelProperty("Oracle")
    Oracle("Oracle");


    DataBaseType(String name){

    }

}
