package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Module;
import com.rayleigh.core.service.BaseService;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface ModuleService extends BaseService {
    List<Module> getAll();

    void deleteById(String id);

    Module findOne(String id);
}
