package com.rayleigh.batman.uiModel;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.Field;
import com.rayleigh.core.annotation.FieldInfo;
import com.rayleigh.core.enums.RelationType;
import com.rayleigh.core.model.BaseModel;

import javax.persistence.*;


public class ModelTableFieldRalationShip extends BaseModel {


    private RelationType relationType;

    private Entities mainEntity;

    private Field mainField;

    private Entities otherEntity;

    private Field otherField;

    private Boolean isCascadeDelete = false;

    private Boolean isLazyFetch;

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

    public Boolean getIsCascadeDelete() {
        return isCascadeDelete;
    }

    public void setIsCascadeDelete(Boolean cascadeDelete) {
        isCascadeDelete = cascadeDelete;
    }

    public Boolean getIsLazyFetch() {
        return isLazyFetch;
    }

    public void setIsLazyFetch(Boolean lazyFetch) {
        isLazyFetch = lazyFetch;
    }
}
