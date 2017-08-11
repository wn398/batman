package com.rayleigh.core.enums;

/**
 * 运算操作符，用于条件表达式中显示条件的计算符号，数据库查询操作符
 */
public enum Operation {
    Equal("equal"),
    NotEqual("notEqual"),
    Like("like"),
    GreaterThan("greatThan"),
    LessThan("lessThan"),
    GreaterThanOrEqualTo("greaterThanOrEqualTo"),
    LessThanOrEqualTo("lessThanOrEqualTo"),
    Between("between"),
    In("in"),
    IsNull("isNull"),
    IsNotNull("isNotNull");


    Operation(String name){};
}
