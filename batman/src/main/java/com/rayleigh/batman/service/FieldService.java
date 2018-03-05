package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.batman.model.Field;
import com.rayleigh.core.service.BaseService;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface FieldService extends BaseService{
    void deleteById(String id);

    Field findOne(String id);

    List<Field> getByEntities(String entityId);
}
