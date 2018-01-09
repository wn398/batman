package com.rayleigh.batman.model;


import com.rayleigh.core.annotation.FieldInfo;
import com.rayleigh.core.model.BaseModel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangn20 on 2017/6/13.
 */
@Entity
@Table(name = "batman_project")
public class Project extends BaseModel {
    @FieldInfo("工程名字")
    @Column
    @NotEmpty(message = "工程名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]+$",message = "工程名不符合规则,数字下划线")
    private String name;

    @FieldInfo("工程描述")
    @Column
    @NotEmpty(message = "工程描述不能为空")
    private String description;

    @FieldInfo("基本包路径名")
    @Column
    @NotEmpty(message = "基本包路径名不能为空")
    private String packageName;

    @FieldInfo("启动端口")
    @Column
    @NotEmpty(message = "启动端口不能为空")
    private String port;

    @FieldInfo("数据源加密")
    @Column
    private Boolean isEncodeDataSource;

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
        return port;
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
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

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
}
