package com.rayleigh.batman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rayleigh.core.annotation.FieldInfo;
import com.rayleigh.core.model.BaseModel;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangn20 on 2017/6/13.
 */
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "batman_module")
public class Module extends BaseModel {
    @FieldInfo("模块名字")
    @Column
    @NotEmpty(message = "模块名字(name)不能为空")
    private String name;

    @FieldInfo("模块描述")
    @Column
    @NotEmpty(message = "模块描述(description)不能为空")
    private String description;
    /**
     * 双向多对多
     */
//    @ManyToMany(targetEntity = Entities.class,fetch = FetchType.LAZY)
//    @JoinTable(name = "more_module_entites",joinColumns = @JoinColumn(name = "module_id",referencedColumnName = "id"),
//    inverseJoinColumns = @JoinColumn(name = "entities_id",referencedColumnName = "id"))
    @OneToMany(mappedBy = "module",cascade = {CascadeType.PERSIST},fetch = FetchType.LAZY)
    private List<Entities> entities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="project_id",nullable = false)
    private Project project;


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

    public List<Entities> getEntities() {
        return entities;
    }

    public void setEntities(List<Entities> entities) {
        if(null!=entities){
            entities.parallelStream().forEach(entity -> entity.setModule(this));
        }
        this.entities = entities;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
