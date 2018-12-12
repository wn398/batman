package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.FieldRelationShip;
import com.rayleigh.batman.model.RelationShip;
import com.rayleigh.batman.repository.FieldRelationShipRepository;
import com.rayleigh.batman.repository.RelationShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/14.
 */
@Service
public class RelationShipServiceImpl implements RelationShipService {
    @Autowired
    private RelationShipRepository relationShipRepository;
    @Autowired
    private FieldRelationShipRepository fieldRelationShipRepository;

    @Override
    public List<RelationShip> save(List<RelationShip> relationShipList) {
        return relationShipRepository.saveAll(relationShipList);
    }

    @Override
    public List<RelationShip> getByMainEntity(Entities entities) {
        return relationShipRepository.getByMainEntity(entities);
    }


    @Override
    public void delete(String id) {
        relationShipRepository.deleteById(id);
    }

    @Override
    public List<RelationShip> getByMainEntityAndOtherEntityIds(String mainEntityId, String otherEntityId) {
        return relationShipRepository.getByMainEntityAndOtherEntityIds(mainEntityId,otherEntityId);
    }

    @Override
    @Transactional
    public void saveRelationShipListAndFieldRelationShip(List<RelationShip> relationShipList, List<FieldRelationShip> fieldRelationShipList) {
        if(null!=relationShipList&&relationShipList.size()>0) {
            relationShipRepository.saveAll(relationShipList);
        }
        if(null!=fieldRelationShipList&&fieldRelationShipList.size()>0) {
            fieldRelationShipRepository.saveAll(fieldRelationShipList);
        }
    }
}
