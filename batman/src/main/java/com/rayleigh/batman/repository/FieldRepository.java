package com.rayleigh.batman.repository;

import com.rayleigh.batman.model.Field;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Repository
public interface FieldRepository extends CustomRepository<Field, String> {
    @Query("select field from Field field where field.entities.id=:entityId")
    List<Field> getByEntities(@Param("entityId") String entityId);

    /**
     *  获取某一个实体是否包含对应属性
     * @param entityId
     * @param fieldName
     * @return
     */
    @Query("select count(field) from Field field where field.entities.id=:entityId and name=:fieldName")
    Long getCountFieldName(@Param("entityId") String entityId,@Param("fieldName") String fieldName);
}
