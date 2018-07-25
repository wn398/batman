package com.rayleigh.batman.util;

import java.util.List;

public class FieldJson {
    private String code;//对应字段名
    private String name;//对应字体描述
    private String delFlag;//是否删除 值为yes,no
    private String dataType;//int,long,double,decimal,bool,composite
    private String must;//是否必须填写  值为yes,no
    private Integer sortNo;//排序编号
    private String relatedFieldName;//关联的字段名字
    private String inputType;//输入类型，单选sinSelect，多选mulSelect，输入框input，文本框text，日期date，调用接口输入interface
    private List<Object> value;//属性的输入值，类型说明里为选项，

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getMust() {
        return must;
    }

    public void setMust(String must) {
        this.must = must;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public String getRelatedFieldName() {
        return relatedFieldName;
    }

    public void setRelatedFieldName(String relatedFieldName) {
        this.relatedFieldName = relatedFieldName;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public List<Object> getValue() {
        return value;
    }

    public void setValue(List<Object> value) {
        this.value = value;
    }
}
