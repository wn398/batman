package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.RelationShip;
import com.rayleigh.core.service.BaseService;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/14.
 */
public interface RelationShipService extends BaseService {
    List<RelationShip> save(List<RelationShip> relationShipList);

    List<RelationShip> getByMainEntity(Entities entities);

    void delete(String id);

    List<RelationShip> getByMainEntityAndOtherEntityIds(String otherEntityId, String mainEntityId);
}
