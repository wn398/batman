package com.rayleigh.core.enums;

import io.swagger.annotations.ApiModel;

/**
 * Created by wangn20 on 2017/6/13.
 * 存储数据类型
 */
@ApiModel("数据类型")
public enum DataType {
    String("String"),
    Integer("Integer"),
    Long("Long"),
    BigDecimal("BigDecimal"),
    Double("Double"),
    Date("Date"),
    Boolean("Boolean");

    DataType(String name){}
}
