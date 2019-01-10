package com.rayleigh.batman.repository;

import com.rayleigh.batman.model.Module;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface ModuleRepository extends CustomRepository<Module, String> {

}
