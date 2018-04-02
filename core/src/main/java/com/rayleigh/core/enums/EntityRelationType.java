package com.rayleigh.core.enums;

/**
 * 实体与实体之间的关系类型
 */
public enum EntityRelationType {
    //表关联关系
    TableType("TableType"),
    //字段关联关系
    FieldType("FieldType"),
    //没有关联关系
    NoneType("NoneType");
    EntityRelationType(String name){}
}
