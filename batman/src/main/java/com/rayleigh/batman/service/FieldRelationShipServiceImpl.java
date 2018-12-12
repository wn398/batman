package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.FieldRelationShip;
import com.rayleigh.batman.model.RelationShip;
import com.rayleigh.batman.repository.FieldRelationShipRepository;
import com.rayleigh.batman.repository.RelationShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/14.
 */
@Service
public class FieldRelationShipServiceImpl implements FieldRelationShipService {
    @Autowired
    private FieldRelationShipRepository fieldRelationShipRepository;

    @Override
    public List<FieldRelationShip> save(List<FieldRelationShip> relationShipList) {
        return fieldRelationShipRepository.saveAll(relationShipList);
    }

    @Override
    public List<FieldRelationShip> getByMainEntity(Entities entities) {
        return fieldRelationShipRepository.getByMainEntity(entities);
    }


    @Override
    public void delete(String id) {
        fieldRelationShipRepository.deleteById(id);
    }

    @Override
    public List<FieldRelationShip> getByMainEntityAndOtherEntityIds(String mainEntityId, String otherEntityId) {
        return fieldRelationShipRepository.getByMainEntityAndOtherEntityIds(mainEntityId,otherEntityId);
    }
}
