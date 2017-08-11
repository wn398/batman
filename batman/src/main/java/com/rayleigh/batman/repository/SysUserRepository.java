package com.rayleigh.batman.repository;

import com.rayleigh.batman.model.SysUser;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Repository
public interface SysUserRepository extends CustomRepository<SysUser, String> {
}
