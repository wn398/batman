package com.rayleigh.batman.repository;

import com.rayleigh.batman.model.SysUser;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Repository
public interface SysUserRepository extends CustomRepository<SysUser, String> {
    List<SysUser> findByNameAndPassword(String name,String password);
}
