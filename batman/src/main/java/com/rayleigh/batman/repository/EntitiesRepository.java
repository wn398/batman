package com.rayleigh.batman.repository;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Repository
public interface EntitiesRepository extends CustomRepository<Entities, String> {

    //获取实体类里字段更新时间最大的
    @Query("select max (field.updateDate) from Field field where field.entities.id =:entityId")
    Date getMaxFieldUpdateDateByEntityId(@Param("entityId") String entityId);

    //获取实体类里方法所有相关更新时间最大的
    @Query("select new List(max (method.updateDate),max(condition.updateDate),max(result.updateDate),max (field.updateDate)) from SearchMethod method,SearchCondition condition,SearchResult result,Field field where method.entities.id=:entityId and method.id = condition.searchMethod.id and method.id = result.searchMethod.id and (field.id = condition.field.id or field.id = result.field.id)")
    List<List<Date>> getMaxMethodUpdateDateByEntityId(@Param("entityId") String entityId);

    //查询实体类关联关系相关的所有更新时间
    @Query("select new List(max(relationShip.updateDate),max(entity.updateDate)) from Entities  entity,RelationShip relationShip where relationShip.mainEntity.id = :entityId and entity.id = relationShip.otherEntity.id")
    List<List<Date>> getMaxRelationShipUpdateDateByEntityId(@Param("entityId") String entityId);

    //查询实体类字段关联关系的所有更新时间
    @Query("select new List(max(field.updateDate),max(relation.updateDate),max(entity.updateDate)) from Entities entity,FieldRelationShip relation,Field field where relation.mainEntity.id=:entityId and entity.id = relation.otherEntity.id and (field.id = relation.mainField.id or field.id = relation.otherField.id)")
    List<List<Date>> getMaxFieldRelationShipUpdateDateByEntityId(@Param("entityId") String entityId);
}

