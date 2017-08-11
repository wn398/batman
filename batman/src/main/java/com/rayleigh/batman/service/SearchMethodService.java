package com.rayleigh.batman.service;

import com.rayleigh.batman.model.SearchMethod;
import com.rayleigh.core.service.BaseService;

public interface SearchMethodService extends BaseService{
    SearchMethod save(SearchMethod searchMethod);

    void delete(String id);

    SearchMethod findOne(String id);
}
