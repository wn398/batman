package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Entities;
import com.rayleigh.core.service.BaseService;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface EntityService extends BaseService{
    Entities save(Entities entities);

    Entities partUpdate(Entities entities);

    Entities findOne(String id);

    void deleteOne(Entities entities);

}
