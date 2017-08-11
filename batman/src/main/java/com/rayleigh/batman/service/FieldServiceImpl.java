package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Field;
import com.rayleigh.batman.repository.FieldRepository;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Service
public class FieldServiceImpl implements FieldService {
    @Autowired
    private FieldRepository fieldRepository;

    @Override
    public void deleteById(String id) {
        fieldRepository.delete(id);
    }

    @Override
    public Field findOne(String id) {
        return fieldRepository.findOne(id);
    }

}
