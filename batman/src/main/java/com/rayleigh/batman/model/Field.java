package com.rayleigh.batman.model;

import com.rayleigh.core.enums.DataType;
import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/12.
 */
@DynamicInsert
@DynamicUpdate
@javax.persistence.Entity
@javax.persistence.Table(name = "batman_field")
public class Field extends BaseModel{
    @ApiModelProperty("名称")
    @Column
    @NotEmpty
    private String name;

    @ApiModelProperty("属性描述")
    @Column
    private String description;

    @ApiModelProperty("数据类型")
    @Enumerated(EnumType.STRING)
    @Column
    private DataType dataType;

    @ApiModelProperty("长度")
    @Column
    private Integer size;

    @ApiModelProperty("默认值")
    @Column
    private String defaultValue;

    @ApiModelProperty("是否非空")
    @Column(length = 1)
    private boolean isNull = true;

    @ApiModelProperty("是否唯一")
    @Column
    private boolean isUnique = false;

    @ApiModelProperty("是否索引")
    @Column
    private boolean isIndex = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    private Entities entities;

    @ApiModelProperty("字段验证信息，用||隔开")
    @Column
    private String validMessage;
    //为了在删除一个字段时，同时把对应的条件和结果都删除掉，级联删除
    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy = "field",fetch = FetchType.LAZY)
    private List<SearchCondition> conditionList = new ArrayList<>();
    //为了在删除一个字段时，同时把对应的条件和结果都删除掉，级联删除
    @OneToMany(cascade = {CascadeType.REMOVE},mappedBy = "field",fetch = FetchType.LAZY)
    private List<SearchResult> resultList = new ArrayList<>();

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public String getName() {
        if(null!=name){
            return name.trim();
        }else {
            return name;
        }
    }

    public void setName(String name) {
        if(StringUtil.isEmpty(name)){
            this.name = null;
        }else {
            this.name = name.trim();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(StringUtil.isEmpty(description)){
            this.description = "没有描述!";
        }else {
            this.description = description;
        }
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean getIsNull() {
        return isNull;
    }

    public void setIsNull(boolean aNull) {
        isNull = aNull;
    }

    public boolean getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(boolean unique) {
        isUnique = unique;
    }

    public boolean getIsIndex() {
        return isIndex;
    }

    public void setIsIndex(boolean index) {
        isIndex = index;
    }

    public String getValidMessage() {
        return validMessage;
    }

    public void setValidMessage(String validMessage) {
        if(StringUtil.isEmpty(validMessage)){
            this.validMessage = null;
        }else {
            this.validMessage = validMessage;
        }
    }

    public List<SearchCondition> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<SearchCondition> conditionList) {
        this.conditionList = conditionList;
    }

    public List<SearchResult> getResultList() {
        return resultList;
    }

    public void setResultList(List<SearchResult> resultList) {
        this.resultList = resultList;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        if(StringUtil.isEmpty(defaultValue)){
            this.defaultValue = null;
        }else {
            this.defaultValue = defaultValue;
        }
    }
}
