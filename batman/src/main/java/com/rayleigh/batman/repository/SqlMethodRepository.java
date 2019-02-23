package com.rayleigh.batman.repository;

import com.rayleigh.batman.model.SearchCondition;
import com.rayleigh.batman.model.SqlMethod;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SqlMethodRepository extends CustomRepository<SqlMethod, String> {

}
