package com.rayleigh.batman.model;

import com.rayleigh.core.enums.DataBaseType;
import com.rayleigh.core.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
@DynamicInsert
@DynamicUpdate
@javax.persistence.Entity
@javax.persistence.Table(name = "batman_project_data_source")
public class ProjectDataSource extends BatmanBaseModel{

    @ApiModelProperty("数据库类型")
    @Column
    @Enumerated(EnumType.STRING)
    private DataBaseType  dataBaseType;

    @ApiModelProperty("数据源名称")
    @Column
    private String  dataSourceNickName;

    @ApiModelProperty("机器名")
    @Column
    private String hostName;

    @ApiModelProperty("端口号")
    @Column
    private Integer port;

    @ApiModelProperty("数据库名")
    @Column
    private String dataBaseName;

    @ApiModelProperty("是否是主数据源")
    @Column
    private boolean isMainDataSource=false;

    @ApiModelProperty("备注")
    @Column
    private String markup;

    @ApiModelProperty("用户名")
    @Column
    private String username;

    @ApiModelProperty("密码")
    @Column
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="project_id",nullable = false)
    private Project project;


    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public String getDataSourceNickName() {
        return dataSourceNickName;
    }

    public void setDataSourceNickName(String dataSourceNickName) {
        this.dataSourceNickName = dataSourceNickName;
    }

    public String getHostName() {
        if(null!=hostName){
            return hostName.trim();
        }else{
            return hostName;
        }

    }

    public void setHostName(String hostName) {
        if(null!=hostName){
            this.hostName = hostName.trim();
        }else{
            this.hostName = hostName;
        }

    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDataBaseName() {
        if(null!=dataBaseName){
            return dataBaseName.trim();
        }else{
            return dataBaseName;
        }
    }

    public void setDataBaseName(String dataBaseName) {
        if(null!=dataBaseName){
            this.dataBaseName = dataBaseName.trim();
        }else{
            this.dataBaseName = dataBaseName;
        }

    }

    public boolean getIsMainDataSource() {
        return isMainDataSource;
    }

    public void setIsMainDataSource(boolean mainDataSource) {
        this.isMainDataSource = mainDataSource;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getMarkup() {
        return markup;
    }

    public void setMarkup(String markup) {
        this.markup = markup;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
