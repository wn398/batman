package com.rayleigh.batman.service;

import com.rayleigh.batman.model.SysUser;
import com.rayleigh.core.service.BaseService;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface SysUserService {
    List<SysUser> findByNameAndPassword(String name,String password);

    SysUser save(SysUser sysUser);

    SysUser findOne(String id);

    List<SysUser> findByName(String name);
}
