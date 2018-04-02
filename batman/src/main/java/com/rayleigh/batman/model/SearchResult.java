package com.rayleigh.batman.model;

import com.rayleigh.batman.util.SearchDBUtil;
import com.rayleigh.core.annotation.FieldInfo;
import com.rayleigh.core.enums.OrderBy;
import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.model.SearchMethodResultModel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

@Entity
@Table(name = "batman_search_result")
public class SearchResult extends BaseModel{

    @FieldInfo("查询结果名字，主要用于id,createDate,updateDate,用(实体id_属性名)表示")
    @Column
    @NotEmpty
    private String fieldName;

    @FieldInfo("排序优先级别,默认0，默认按主对象的updateDate倒序")
    @Column
    private Integer orderByNum = 0;

    @FieldInfo("排序类型")
    @Enumerated(EnumType.STRING)
    @Column
    private OrderBy orderByType;

    @FieldInfo("存放字段")
    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;

    @FieldInfo("那个查询方法上的结果")
    @ManyToOne
    @JoinColumn(name = "search_method_id")
    private SearchMethod searchMethod;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public SearchMethod getSearchMethod() {
        return searchMethod;
    }

    public void setSearchMethod(SearchMethod searchMethod) {
        this.searchMethod = searchMethod;
    }

    public Integer getOrderByNum() {
        return orderByNum;
    }

    public void setOrderByNum(Integer orderByNum) {
        this.orderByNum = orderByNum;
    }

    public OrderBy getOrderByType() {
        return orderByType;
    }

    public void setOrderByType(OrderBy orderByType) {
        this.orderByType = orderByType;
    }

    public static SearchMethodResultModel toResultModel(SearchResult searchResult){
        SearchMethodResultModel searchMethodResultModel = new SearchMethodResultModel();
        searchMethodResultModel.setEntityName(SearchDBUtil.getEntityName(searchResult.getFieldName().split("_")[0]));
        searchMethodResultModel.setFieldName(searchResult.getFieldName().split("_")[1]);
        searchMethodResultModel.setOrderByNum(searchResult.getOrderByNum());
        searchMethodResultModel.setOrderByType(searchResult.getOrderByType());
        return searchMethodResultModel;
    }
}
