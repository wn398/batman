package com.rayleigh.batman.uiModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public interface ProjectListModel {
//    @ApiModelProperty("主键id")
//    private String id;
//    @ApiModelProperty("名称")
//    private String name;
//    @ApiModelProperty("项目描述")
//    private String description;
//    @ApiModelProperty("项目基本路径")
//    private String packageName;
//    @ApiModelProperty("模块数量")
//    private Long moduleNum;
//    @ApiModelProperty("实体数量")
//    private Long entityNum;
//    @ApiModelProperty("创建时间")
//    private Date createDate;
//    @ApiModelProperty("更新时间")
//    private Date hierachyDate;
//    @ApiModelProperty("版本")
//    private Long version;
//
//
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Long getModuleNum() {
//        return moduleNum;
//    }
//
//    public void setModuleNum(Long moduleNum) {
//        this.moduleNum = moduleNum;
//    }
//
//    public Long getEntityNum() {
//        return entityNum;
//    }
//
//    public void setEntityNum(Long entityNum) {
//        this.entityNum = entityNum;
//    }
//
//    public Date getCreateDate() {
//        return createDate;
//    }
//
//    public void setCreateDate(Date createDate) {
//        this.createDate = createDate;
//    }
//
//    public Date getHierachyDate() {
//        return hierachyDate;
//    }
//
//    public void setHierachyDate(Date hierachyDate) {
//        this.hierachyDate = hierachyDate;
//    }
//
//    public Long getVersion() {
//        return version;
//    }
//
//    public void setVersion(Long version) {
//        this.version = version;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getPackageName() {
//        return packageName;
//    }
//
//    public void setPackageName(String packageName) {
//        this.packageName = packageName;
//    }

    public String getId();



    public String getName();


    public Long getModuleNum();
    public Long getEntityNum() ;


    public Date getCreateDate();

    public Date getHierachyDate();


    public Long getVersion();


    public String getDescription();

    public String getPackageName();

}
