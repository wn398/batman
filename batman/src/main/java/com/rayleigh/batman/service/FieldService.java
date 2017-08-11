package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Field;
import com.rayleigh.core.service.BaseService;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface FieldService extends BaseService{
    void deleteById(String id);

    Field findOne(String id);
}
