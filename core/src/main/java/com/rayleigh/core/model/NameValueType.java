package com.rayleigh.core.model;

import com.rayleigh.core.enums.DataType;

/**
 * 用来存储字段名字，字段值，字段类型
 */
public class NameValueType {
    private String name;
    private Object value;
    private DataType dataType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
