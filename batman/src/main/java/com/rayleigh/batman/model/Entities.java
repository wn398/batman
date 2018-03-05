package com.rayleigh.batman.model;

import com.rayleigh.core.annotation.FieldInfo;
import com.rayleigh.core.enums.PrimaryKeyType;
import com.rayleigh.core.model.BaseModel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "batman_entity")
public class Entities extends BaseModel {

    @FieldInfo("实体名字")
    @Column
    @NotEmpty(message = "实体名字不能空")
    //@Pattern(regexp = "",message = "不符合规则")
    private String name;

    @FieldInfo("实体描述")
    @Column
    @NotEmpty(message = "实体名字不能空")
    private String description;

    @FieldInfo("主键类型")
    @Column
    @Enumerated(EnumType.STRING)
    private PrimaryKeyType primaryKeyType;

    @FieldInfo("是否启用表名前辍")
    @Column
    private Boolean addPrefix;
    /**
     * 双向一对多，多对一
     */
    @Valid
    @OneToMany(mappedBy = "entities",cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.MERGE},orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Field> fields = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id",nullable = false)
    @NotNull
    private Project project;

    @OneToMany(fetch = FetchType.LAZY,cascade = {CascadeType.REMOVE,CascadeType.MERGE},mappedBy = "entities")
    private List<SearchMethod> methods;
    /**
     * 双向多对多
     */
//    @ManyToMany(targetEntity = Module.class,cascade = {CascadeType.PERSIST},fetch = FetchType.LAZY)
//    @JoinTable(name = "more_module_entites",joinColumns = @JoinColumn(name = "entities_id",referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "module_id",referencedColumnName = "id"))
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @OneToMany(mappedBy = "mainEntity",fetch = FetchType.LAZY)
    private List<RelationShip> mainEntityRelationShips = new ArrayList<>();

    @OneToMany(mappedBy = "otherEntity",fetch = FetchType.LAZY)
    private List<RelationShip> otherEntityRelationShips = new ArrayList<>();

    @OneToMany(mappedBy = "mainEntity",fetch = FetchType.LAZY)
    private List<FieldRelationShip> mainFieldRelationShips = new ArrayList<>();

    @OneToMany(mappedBy = "otherEntity",fetch = FetchType.LAZY)
    private List<FieldRelationShip> otherFieldRelationShips = new ArrayList<>();

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        if(null!=project)
        project.getEntities().add(this);
        this.project = project;
    }

    public PrimaryKeyType getPrimaryKeyType() {
        return primaryKeyType;
    }

    public void setPrimaryKeyType(PrimaryKeyType primaryKeyType) {
        this.primaryKeyType = primaryKeyType;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
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

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        if(null!=fields) {
            fields.parallelStream().forEach(field -> field.setEntities(this));
        }
        this.fields = fields;
    }

    public List<RelationShip> getMainEntityRelationShips() {
        return mainEntityRelationShips;
    }

    public void setMainEntityRelationShips(List<RelationShip> mainEntityRelationShips) {
        if(null!=mainEntityRelationShips){
            mainEntityRelationShips.parallelStream().forEach(relationShip -> relationShip.setMainEntity(this));
        }
        this.mainEntityRelationShips = mainEntityRelationShips;
    }

    public List<RelationShip> getOtherEntityRelationShips() {
        return otherEntityRelationShips;
    }

    public void setOtherEntityRelationShips(List<RelationShip> otherEntityRelationShips) {
        if(null!=otherEntityRelationShips){
            otherEntityRelationShips.parallelStream().forEach(relationShip -> relationShip.setOtherEntity(this));
        }
        this.otherEntityRelationShips = otherEntityRelationShips;
    }

    public List<SearchMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<SearchMethod> methods) {
        if(null != methods){
            methods.parallelStream().forEach(method ->method.setEntities(this));
        }
        this.methods = methods;
    }

    public List<FieldRelationShip> getMainFieldRelationShips() {
        return mainFieldRelationShips;
    }

    public void setMainFieldRelationShips(List<FieldRelationShip> mainFieldRelationShips) {
        if(null !=mainFieldRelationShips){
            mainFieldRelationShips.parallelStream().forEach(fieldRelationShip -> fieldRelationShip.setMainEntity(this));
        }
        this.mainFieldRelationShips = mainFieldRelationShips;
    }

    public List<FieldRelationShip> getOtherFieldRelationShips() {
        return otherFieldRelationShips;
    }

    public void setOtherFieldRelationShips(List<FieldRelationShip> otherFieldRelationShips) {
        if(null!=otherFieldRelationShips){
            otherFieldRelationShips.parallelStream().forEach(fieldRelationShip -> fieldRelationShip.setOtherEntity(this));
        }
        this.otherFieldRelationShips = otherFieldRelationShips;
    }

    public Boolean getAddPrefix() {
        return addPrefix;
    }

    public void setAddPrefix(Boolean addPrefix) {
        this.addPrefix = addPrefix;
    }
}
