package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Module;
import com.rayleigh.core.service.BaseService;

import java.util.Date;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface ModuleService {
    List<Module> getAll();

    void deleteById(String id);

    Module findOne(String id);

    void setUpdateDate(String id,Date updateDate);
}
