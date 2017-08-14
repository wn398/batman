package com.rayleigh.core.model;

import java.util.List;

/**
 * 获取对象id加关系的模型
 */
public class RelationModel {
    private String mainObjId;
    private List<String> subObjName;

    public String getMainObjId() {
        return mainObjId;
    }

    public void setMainObjId(String mainObjId) {
        this.mainObjId = mainObjId;
    }

    public List<String> getSubObjName() {
        return subObjName;
    }

    public void setSubObjName(List<String> subObjName) {
        this.subObjName = subObjName;
    }
}
