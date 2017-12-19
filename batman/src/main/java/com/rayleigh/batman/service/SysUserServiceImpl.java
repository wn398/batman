package com.rayleigh.batman.service;

import com.rayleigh.batman.model.SysUser;
import com.rayleigh.batman.repository.SysUserRepository;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    public List<SysUser> findByNameAndPassword(String name, String password) {
        return sysUserRepository.findByNameAndPassword(name,password);
    }

    @Override
    public SysUser save(SysUser sysUser) {
        return sysUserRepository.save(sysUser);
    }

    @Override
    public SysUser findOne(String id) {
        return sysUserRepository.findOne(id);
    }

    @Override
    public List<SysUser> findByName(String name) {
        return sysUserRepository.findAll(new Specification<SysUser>() {
            @Override
            public Predicate toPredicate(Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("name"),name);
            }
        });
    }


}
