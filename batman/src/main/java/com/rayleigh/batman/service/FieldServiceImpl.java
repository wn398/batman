package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.Field;
import com.rayleigh.batman.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Service
public class FieldServiceImpl implements FieldService {
    @Autowired
    private FieldRepository fieldRepository;

    @Override
    public void deleteById(String id) {
        fieldRepository.deleteById(id);
    }

    @Override
    public Field findOne(String id) {
        return fieldRepository.findById(id).get();
    }

    @Override
    public List<Field> getByEntities(String entityId) {
        return fieldRepository.getByEntities(entityId);
    }

    @Override
    public Boolean isContainFiledName(String entityId, String fieldName) {

        Long count= fieldRepository.getCountFieldName(entityId,fieldName);
        if(count>0){
            return true;
        }else{
            return false;
        }
    }


}
