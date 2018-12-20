package com.rayleigh.batman.model;

import com.rayleigh.core.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.engine.internal.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "batman_search_method")
public class SearchMethod extends BatmanBaseModel{
    @ApiModelProperty("方法名称")
    @Column
    @NotEmpty
    private String methodName;

    @ApiModelProperty("方法描述")
    @Column
    @NotEmpty
    private String description;

    @ApiModelProperty("是否提供接口出去")
    @Column
    private Boolean isInterface = true;
    //返回对象类型，就会过滤出主对象，否则就是返回选中的字段包装对象
    @ApiModelProperty("是否返回对象类型")
    @Column
    private Boolean isReturnObject = false;

    @ApiModelProperty("是否动态查询")
    @Column
    private Boolean isDynamicSearch = false;

    @ApiModelProperty("所对应的查询条件")
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "searchMethod",cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE},orphanRemoval = true)
    private List<SearchCondition> searchConditions;

    @ApiModelProperty("哪个实体上的查询方法")
    @ManyToOne
    @JoinColumn(name = "entity_id")
    private Entities entities;

    @ApiModelProperty("存放方法返回的结果")
    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE},orphanRemoval = true,mappedBy = "searchMethod")
    private List<SearchResult> searchResults;


    public String getMethodName() {
        if(null!=methodName){
            return methodName.trim();
        }else{
            return methodName;
        }

    }

    public void setMethodName(String methodName) {
        if(null!=methodName){
            this.methodName = methodName.trim();
        }else{
            this.methodName = methodName;
        }
    }

    public List<SearchCondition> getConditionList() {
        return searchConditions;
    }

    public void setConditionList(List<SearchCondition> searchConditions) {
        if(null !=searchConditions){
            searchConditions.parallelStream().forEach(condition->condition.setSearchMethod(this));
        }
        this.searchConditions = searchConditions;
    }

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        if(null!=searchResults){
            searchResults.parallelStream().forEach(searchResult -> searchResult.setSearchMethod(this));
        }
        this.searchResults = searchResults;
    }

    public Boolean getIsInterface() {
        return isInterface;
    }

    public void setIsInterface(Boolean anInterface) {
        isInterface = anInterface;
    }

    public Boolean getIsReturnObject() {
        return isReturnObject;
    }

    public void setIsReturnObject(Boolean returnObject) {
        isReturnObject = returnObject;
    }

    public Boolean getIsDynamicSearch() {
        return isDynamicSearch;
    }

    public void setIsDynamicSearch(Boolean dynamicSearch) {
        isDynamicSearch = dynamicSearch;
    }
}
