package com.rayleigh.core.enums;

/**
 * 数据库查询参数操作的值类型
 */
public enum SearchValueType {
    Single("single"),//单值
    Section("section"),//区间
    List("list");//列表



    SearchValueType(String name){}
}
