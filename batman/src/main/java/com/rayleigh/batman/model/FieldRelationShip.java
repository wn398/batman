package com.rayleigh.batman.model;

import com.rayleigh.core.enums.RelationType;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by wangn20 on 2017/6/14.
 * 表关联关系，两张表之间的关系
 */
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "batman_field_relationship")
public class FieldRelationShip extends BatmanBaseModel {

    @ApiModelProperty("映射关系")
    @Enumerated(EnumType.STRING)
    @Column
    private RelationType relationType;

    /**
     * 关联关系的主对象
     */
    @ManyToOne
    @JoinColumn(name = "main_entity_id")
    private Entities mainEntity;
    /**
     * 关联主对象字段
     */
    @ManyToOne
    @JoinColumn(name = "main_field_id")
    private Field mainField;

    /**
     * 关联关系其他对象
     */
    @ManyToOne
    @JoinColumn(name = "other_entity_id")
    private Entities otherEntity;
    /**
     * 关联关系其他对象字段
     */
    @ManyToOne
    @JoinColumn(name = "other_field_id")
    private Field otherField;

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public Entities getMainEntity() {
        return mainEntity;
    }

    public void setMainEntity(Entities mainEntity) {
        this.mainEntity = mainEntity;
    }

    public Entities getOtherEntity() {
        return otherEntity;
    }

    public void setOtherEntity(Entities otherEntity) {
        this.otherEntity = otherEntity;
    }

    public Field getMainField() {
        return mainField;
    }

    public void setMainField(Field mainField) {
        this.mainField = mainField;
    }

    public Field getOtherField() {
        return otherField;
    }

    public void setOtherField(Field otherField) {
        this.otherField = otherField;
    }
}
