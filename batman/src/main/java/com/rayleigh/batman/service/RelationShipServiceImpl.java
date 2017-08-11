package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.RelationShip;
import com.rayleigh.batman.repository.RelationShipRepository;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/14.
 */
@Service
public class RelationShipServiceImpl implements RelationShipService {
    @Autowired
    private RelationShipRepository relationShipRepository;

    @Override
    public List<RelationShip> save(List<RelationShip> relationShipList) {
        return relationShipRepository.save(relationShipList);
    }

    @Override
    public List<RelationShip> getByMainEntity(Entities entities) {
        return relationShipRepository.getByMainEntity(entities);
    }


    @Override
    public void delete(String id) {
        relationShipRepository.delete(id);
    }

    @Override
    public List<RelationShip> getByMainEntityAndOtherEntityIds(String mainEntityId, String otherEntityId) {
        return relationShipRepository.getByMainEntityAndOtherEntityIds(mainEntityId,otherEntityId);
    }
}
