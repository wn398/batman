package com.rayleigh.batman.repository;
import com.rayleigh.batman.model.Entities;
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
public interface RelationShipRepository extends CustomRepository<RelationShip, String> {
    List<RelationShip> getByMainEntity(Entities entities);
    @Query("select relationShip from RelationShip relationShip where relationShip.mainEntity.id=:mainEntityId and relationShip.otherEntity.id=:otherEntityId")
    List<RelationShip> getByMainEntityAndOtherEntityIds(@Param("mainEntityId") String mainEntityId, @Param("otherEntityId") String otherEntityId);
}
