package com.rayleigh.core.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("数据库枚举")
public enum DataBaseType {
    @ApiModelProperty("mysql")
    MySQL("MySQL"),
    @ApiModelProperty("pgsql")
    PostgreSql("PostgreSql");


    DataBaseType(String name){

    }

}
