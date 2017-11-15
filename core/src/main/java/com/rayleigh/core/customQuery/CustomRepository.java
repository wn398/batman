package com.rayleigh.core.customQuery;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.Query;
import javax.persistence.Tuple;

@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable>extends JpaRepository<T, ID> ,JpaSpecificationExecutor<T>{

    Page<T> findByAuto(T example,Pageable pageable);
    List<Object[]> listBySQL(String sql);
    Query getBySQL(String sql);
    Query getByHQL(String hql);
    Integer updateAll(Specification<T> specification, Map<String,Object> needUpdatedNameValue);
    Integer deleteAll(Specification<T> specification);
    Long getCount(Specification<T> specification);
}