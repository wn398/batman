package com.rayleigh.batman.model;

import com.rayleigh.core.enums.MappingDirection;
import com.rayleigh.core.enums.RelationType;
import com.rayleigh.core.model.BaseModel;
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
@Table(name = "batman_relationship")
public class RelationShip extends BatmanBaseModel {

    @ApiModelProperty("映射关系")
    @Enumerated(EnumType.STRING)
    @Column
    private RelationType relationType;

    @ApiModelProperty("是否级联删除")
    @Column
    private Boolean isCascadeDelete = false;
// 删除，默认级联更新
//    @ApiModelProperty("是否级联更新")
//    @Column
//    private Boolean isCascadeUpdate;

    @ApiModelProperty("是否懒加载")
    @Column
    private Boolean isLazyFetch;


    /**
     * 关联关系的主对象
     */
    @ManyToOne
    @JoinColumn(name = "main_entity_id")
    private Entities mainEntity;

    /**
     * 关联关系其他对象
     */
    @ManyToOne
    @JoinColumn(name = "other_entity_id")
    private Entities otherEntity;


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

    public Boolean getIsCascadeDelete() {
        return isCascadeDelete;
    }

    public void setIsCascadeDelete(Boolean cascadeDelete) {
        isCascadeDelete = cascadeDelete;
    }

//    public Boolean getIsCascadeUpdate() {
//        return isCascadeUpdate;
//    }
//
//    public void setIsCascadeUpdate(Boolean cascadeUpdate) {
//        isCascadeUpdate = cascadeUpdate;
//    }

    public Boolean getIsLazyFetch() {
        return isLazyFetch;
    }

    public void setIsLazyFetch(Boolean lazyFetch) {
        isLazyFetch = lazyFetch;
    }
}
