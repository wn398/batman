package com.rayleigh.batman.service;

import com.rayleigh.batman.model.SqlMethod;

public interface SqlMethodService {
    void delete(String id);

    void add(SqlMethod sqlMethod);

    SqlMethod findOne(String id);

    void update(SqlMethod sqlMethod);
}
