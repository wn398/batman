package com.rayleigh.batman.service;

import com.rayleigh.batman.model.SqlMethod;
import com.rayleigh.batman.repository.SqlMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SqlMethodServiceImpl implements SqlMethodService {
    @Autowired
    private SqlMethodRepository sqlMethodRepository;

    @Override
    public void delete(String id) {
        sqlMethodRepository.deleteById(id);
    }

    @Override
    public void add(SqlMethod sqlMethod) {
        sqlMethodRepository.save(sqlMethod);
    }

    @Override
    public SqlMethod findOne(String id) {
        return sqlMethodRepository.findById(id).get();
    }

    @Override
    public void update(SqlMethod sqlMethod) {
        sqlMethodRepository.save(sqlMethod);
        return;
    }
}
