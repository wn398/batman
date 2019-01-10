package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Module;
import com.rayleigh.batman.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Service
public class ModuleServiceImpl implements ModuleService {
    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public List<Module> getAll() {
        return moduleRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        moduleRepository.deleteById(id);
    }

    @Override
    public Module findOne(String id) {
        return moduleRepository.findById(id).get();
    }

    @Override
    public void setUpdateDate(String id, Date updateDate) {
        moduleRepository.updateAll(new Specification<Module>() {
            @Override
            public Predicate toPredicate(Root<Module> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("id"),id);
            }
        }, Collections.singletonMap("updateDate",updateDate));
    }

}
