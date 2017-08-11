package com.rayleigh.batman.service;

import com.rayleigh.batman.repository.SysUserRepository;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserRepository sysUserRepository;

}
