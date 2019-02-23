package com.rayleigh.batman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rayleigh.batman.uiModel.FieldInfo;
import com.rayleigh.core.enums.DataBaseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@ApiModel("方法")
@Entity
@Table(name = "batman_sql_method")
public class SqlMethod extends BatmanBaseModel{
    @ApiModelProperty("方法名称")
    @Column
    @NotBlank
    private String name;

    @ApiModelProperty("方法描述")
    @Column
    @NotBlank
    private String description;

    @ApiModelProperty("查询sql语句内容")
    @Column(length = 70000)
    @NotBlank
    private String sqlContext;

    @ApiModelProperty("方法是否服务化")
    @Column
    private Boolean isRestful;

    @ApiModelProperty("服务化方法必须有访问路径(实体controller路径后面)")
    @Column
    private String visitPath;

    @ApiModelProperty("方法所在实体")
    @ManyToOne
    @JoinColumn(name = "entity_id")
    private Entities entities;

    @ApiModelProperty("是否分页")
    @Column
    private Boolean isPage;

    @ApiModelProperty("方法请求参数 json")
    @JsonIgnoreProperties
    @JsonIgnore
    @Column(length = 70000)
    private String paramListJson;

    @ApiModelProperty("参数列表")
    @Transient
    private List<FieldInfo> paramList;

    @ApiModelProperty("参数包装类名")
    @Column
    private String paramClassName;

    @ApiModelProperty("方法返回字段list Json")
    @JsonIgnoreProperties
    @JsonIgnore
    @Column(length = 70000)
    private String resultJson;

    @ApiModelProperty("结果列表")
    @Transient
    private List<FieldInfo> resultList;

    @ApiModelProperty("方法结果包装类名")
    @Column
    private String resultClassName;

    @ApiModelProperty("返回结构是不是实体类")
    @Column
    private Boolean IsResultEntity;

    @ApiModelProperty("数据源")
    @Column
    private String dataSource;

    @ApiModelProperty("数据库类型")
    @Column
    @Enumerated(EnumType.STRING)
    private DataBaseType dataBaseType;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsRestful() {
        return isRestful;
    }

    public void setIsRestful(Boolean restful) {
        isRestful = restful;
    }

    public String getVisitPath() {
        return visitPath;
    }

    public void setVisitPath(String visitPath) {
        this.visitPath = visitPath;
    }

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public Boolean getIsPage() {
        return isPage;
    }

    public void setIsPage(Boolean page) {
        isPage = page;
    }

    public String getParamListJson() {
        return paramListJson;
    }

    public void setParamListJson(String paramListJson) {
        this.paramListJson = paramListJson;
    }

    public List<FieldInfo> getParamList() {
        return paramList;
    }

    public void setParamList(List<FieldInfo> paramList) {
        this.paramList = paramList;
    }

    public String getParamClassName() {
        return paramClassName;
    }

    public void setParamClassName(String paramClassName) {
        this.paramClassName = paramClassName;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public List<FieldInfo> getResultList() {
        return resultList;
    }

    public void setResultList(List<FieldInfo> resultList) {
        this.resultList = resultList;
    }

    public String getResultClassName() {
        return resultClassName;
    }

    public void setResultClassName(String resultClassName) {
        this.resultClassName = resultClassName;
    }

    public Boolean getIsResultEntity() {
        return IsResultEntity;
    }

    public void setIsResultEntity(Boolean resultEntity) {
        IsResultEntity = resultEntity;
    }

    public String getSqlContext() {
        return sqlContext;
    }

    public void setSqlContext(String sqlContext) {
        this.sqlContext = sqlContext;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }
}
