package com.rayleigh.batman.uiModel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import java.util.Date;

@Api("实体列表model")
public class EntityListModel {

    @ApiModelProperty("实体名称")
    private String name;
    @ApiModelProperty("实体id")
    private String id;
    @ApiModelProperty("实体描述")
    private String description;
    @ApiModelProperty("模块名称")
    private String modelDescription;
    @ApiModelProperty("模块id")
    private String modelId;
    @ApiModelProperty("表关联个数")
    private Long tableNum;
    @ApiModelProperty("字段关联个数")
    private Long fieldNum;
    @ApiModelProperty("方法1个数")
    private Long methodNum;
    @ApiModelProperty("sql方法个数")
    private Long methodNum2;
    @ApiModelProperty("创建时间")
    private Date createDate;
    @ApiModelProperty("更新时间")
    private Date updateDate;
    @ApiModelProperty("版本号")
    private Long version;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Long getTableNum() {
        return tableNum;
    }

    public void setTableNum(Long tableNum) {
        this.tableNum = tableNum;
    }

    public Long getFieldNum() {
        return fieldNum;
    }

    public void setFieldNum(Long fieldNum) {
        this.fieldNum = fieldNum;
    }

    public Long getMethodNum() {
        return methodNum;
    }

    public void setMethodNum(Long methodNum) {
        this.methodNum = methodNum;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getMethodNum2() {
        return methodNum2;
    }

    public void setMethodNum2(Long methodNum2) {
        this.methodNum2 = methodNum2;
    }
}
