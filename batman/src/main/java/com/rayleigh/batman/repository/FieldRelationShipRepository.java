package com.rayleigh.batman.repository;
import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.FieldRelationShip;
import com.rayleigh.batman.model.RelationShip;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/14.
 */
@Repository
public interface FieldRelationShipRepository extends CustomRepository<FieldRelationShip, String> {
    List<FieldRelationShip> getByMainEntity(Entities entities);
    @Query("select fieldRelationShip from FieldRelationShip fieldRelationShip where fieldRelationShip.mainEntity.id=:mainEntityId and fieldRelationShip.otherEntity.id=:otherEntityId")
    List<FieldRelationShip> getByMainEntityAndOtherEntityIds(@Param("mainEntityId") String mainEntityId, @Param("otherEntityId") String otherEntityId);
}
