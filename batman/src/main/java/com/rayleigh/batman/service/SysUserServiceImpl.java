package com.rayleigh.batman.service;

import com.rayleigh.batman.model.SysUser;
import com.rayleigh.batman.repository.SysUserRepository;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
