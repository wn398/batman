package com.rayleigh.batman.repository;

import com.rayleigh.batman.model.Field;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Repository
public interface FieldRepository extends CustomRepository<Field, String> {
}
