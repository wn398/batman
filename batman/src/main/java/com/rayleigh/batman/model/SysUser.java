package com.rayleigh.batman.model;

import com.rayleigh.core.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wangn20 on 2017/6/13.
 */
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "batman_sys_user")
public class SysUser extends BaseModel {
    @ApiModelProperty("用户名")
    @Column
    @NotNull
    private String name;

    @ApiModelProperty("密码")
    @Column
    @NotNull
    private String password;

    @ApiModelProperty("电子邮件")
    @Column
    @NotNull
    private String email;

    @OneToMany(mappedBy = "sysUser")
    private List<Project> projects = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        if(null!=projects&&projects.size()>0){
            projects.parallelStream().forEach(project -> project.setSysUser(this));
        }
        this.projects = projects;
    }
}
