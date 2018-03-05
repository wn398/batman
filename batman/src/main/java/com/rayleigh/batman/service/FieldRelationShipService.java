package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.FieldRelationShip;
import com.rayleigh.batman.model.RelationShip;
import com.rayleigh.core.service.BaseService;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/14.
 */
public interface FieldRelationShipService extends BaseService {
    List<FieldRelationShip> save(List<FieldRelationShip> fieldRelationShipList);

    List<FieldRelationShip> getByMainEntity(Entities entities);

    void delete(String id);

    List<FieldRelationShip> getByMainEntityAndOtherEntityIds(String otherEntityId, String mainEntityId);
}
