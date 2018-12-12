package com.rayleigh.core.customQuery;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.Query;

@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable>extends JpaRepository<T, ID> ,JpaSpecificationExecutor<T>{
    /**
     *
     * @param sql
     * @return
     */
    List<Object[]>  listBySQL(String sql);

    /**
     *
     * @param sql
     * @return
     */
    Query getBySQL(String sql);

    /**
     *
     * @param hql
     * @return
     */
    Query getByHQL(String hql);

    /**
     *
     * @param specification
     * @param needUpdatedNameValue
     * @return
     */
    Integer updateAll(Specification<T> specification, Map<String,Object> needUpdatedNameValue);

    /**
     *
     * @param specification
     * @return
     */
    Integer deleteAll(Specification<T> specification);

    /**
     *
     * @param specification
     * @return
     */
    Long getCount(Specification<T> specification);
}