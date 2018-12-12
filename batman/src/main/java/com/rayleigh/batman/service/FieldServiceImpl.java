package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.Field;
import com.rayleigh.batman.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
