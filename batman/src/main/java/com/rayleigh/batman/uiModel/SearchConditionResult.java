package com.rayleigh.batman.uiModel;

import com.rayleigh.batman.model.SearchCondition;
import com.rayleigh.batman.model.SearchResult;
import com.rayleigh.core.enums.DataType;

public class SearchConditionResult {
    public SearchConditionResult(String entityName,String fieldName,DataType fieldDataType,String fieldId){
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.fieldDataType = fieldDataType;
        this.fieldId = fieldId;
    }
    private SearchCondition searchCondition;
    private SearchResult searchResult;

    private String entityName;
    private String fieldName;
    private DataType fieldDataType;
    private String fieldId;

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DataType getFieldDataType() {
        return fieldDataType;
    }

    public void setFieldDataType(DataType fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

    public SearchCondition getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(SearchCondition searchCondition) {
        this.searchCondition = searchCondition;
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }
}
