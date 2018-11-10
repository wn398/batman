package com.rayleigh.batman.model;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.rayleigh.core.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by wangn20 on 2017/6/13.
 */
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "batman_project")
public class Project extends BaseModel {
    @ApiModelProperty("工程名字")
    @Column
    @NotEmpty(message = "工程名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]+$",message = "工程名不符合规则,数字下划线")
    private String name;

    @ApiModelProperty("工程描述")
    @Column
    @NotEmpty(message = "工程描述不能为空")
    private String description;

    @ApiModelProperty("基本包路径名")
    @Column
    @NotEmpty(message = "基本包路径名不能为空")
    private String packageName;

    @ApiModelProperty("启动端口")
    @Column
    @NotEmpty(message = "启动端口不能为空")
    private String port;

    @ApiModelProperty("数据源加密")
    @Column
    private Boolean isEncodeDataSource;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @ApiModelProperty("层级更新日期,主要用于标示下面属性变化")
    private Date hierachyDate;

    @Valid
    @OneToMany(mappedBy = "project",cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.MERGE},fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Module> modules=new ArrayList<>();

    @Valid
    @OneToMany(mappedBy = "project",cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.MERGE},fetch = FetchType.LAZY,orphanRemoval = true)
    private List<ProjectDataSource> projectDataSources=new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sysuser_id")
    private SysUser sysUser;



    @OneToMany(mappedBy = "project",cascade = CascadeType.REMOVE)
    private List<Entities> entities = new ArrayList<>();

    public String getPort() {
        if(null!=port){
            return port.trim();
        }else{
            return port;
        }
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<Entities> getEntities() {
        return entities;
    }

    public void setEntities(List<Entities> entities) {
        if(null!=entities&&entities.size()>0){
            entities.parallelStream().forEach(entities1 -> entities1.setProject(this));
        }
        this.entities = entities;
    }

    public String getPackageName() {
        if(null!=packageName){
            return packageName.trim();
        }else{
            return packageName;
        }

    }

    public void setPackageName(String packageName) {
        if(null!=packageName){
            this.packageName = packageName.trim();
        }else{
            this.packageName = packageName;
        }

    }

    public String getName() {
        if(null!=name){
            return name.trim();
        }else{
            return name;
        }

    }

    public void setName(String name) {
        if(null!=name){
            this.name = name.trim();
        }else{
            this.name = name;
        }

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Module> getModules() {
        return modules;
    }
    //设置modules时把project的关联关系挂上
    public void setModules(List<Module> modules) {

        if(null!=modules&&modules.size()>0){
            modules.stream().forEach(module -> module.setProject(this));
        }
        this.modules = modules;
    }

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public List<ProjectDataSource> getProjectDataSources() {
        return projectDataSources;
    }

    public void setProjectDataSources(List<ProjectDataSource> projectDataSources) {
        if(null!=projectDataSources&&projectDataSources.size()>0){
            projectDataSources.stream().forEach(projectDataSource -> projectDataSource.setProject(this));
        }
        this.projectDataSources = projectDataSources;
    }

    public Boolean getIsEncodeDataSource() {
        return isEncodeDataSource;
    }

    public void setIsEncodeDataSource(Boolean encodeDataSource) {
        this.isEncodeDataSource = encodeDataSource;
    }

    public Date getHierachyDate() {
        if(null!=hierachyDate) {
            return (Date)hierachyDate.clone();
        }else{
            return null;
        }
    }

    public void setHierachyDate(Date hierachyDate) {
        this.hierachyDate = hierachyDate;
    }
}
