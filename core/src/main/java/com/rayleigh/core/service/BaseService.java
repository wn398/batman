package com.rayleigh.core.service;

import com.rayleigh.core.model.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * @Author 三藏
 * Created by wangn20 on 2017/6/29.
 */
public interface BaseService<T extends BaseModel,ID extends Serializable>{
    Logger logger = LoggerFactory.getLogger(BaseService.class);

    /**
     * 使用自己的id来保存实体，而不是自动生成的，不传值保存时为NULL
     * @param t
     * @return
     * @throws Exception
     */
    T saveWithAssignedId(T t)throws Exception;

    /**
     * 同saveWithAssignedId方法，这个是批量保存方法
     * @param ts
     * @return
     * @throws Exception
     */
    List<T> saveWithAssignedId(List<T> ts)throws Exception;

    /**
     * 更新方法
     * @param t
     * @return
     */
    T update(T t);

    /**
     * 保存方法
     * @param t
     * @return
     */
    T save(T t);

    /**
     * 批量保存方法
     * @param ts
     * @return
     */
    List<T> saveAll(List<T> ts);

    /**
     * 通过id删除实体
     * @param id
     */
    void deleteById(ID id);

    /**
     * 查询一系列id的实体
     * @param ids
     * @return
     */
    List<T> findByIds(List<ID> ids);

    /**
     * 查询一系列id的实体，只返回特定属性值
     * @param ids
     * @param propertyNames
     * @return
     */
    List<T> findByIds(List<ID> ids,List<String> propertyNames);

    /**
     * 通过id查询单个实体
     * @param id
     * @return
     */
    T findOne(ID id);

    /**
     * 查询单个实体，只返回特定属性
     * @param id
     * @param propertyNames
     * @return
     */
    T findOne(ID id,List<String> propertyNames);

    /**
     * 通过本地sql查询，返回查询的列表多个列时List<Object[]> 单个列时List<Object>
     * @param sql
     * @return
     */
    List findBySQL(String sql);

    /**
     * 查询满足一个或多个相等条件的一条结果
     * @param map
     * @return
     */
    T findOneByProperties(Map<String,Object> map);

    /**
     * 查询满足一个或多个相等条件的一条结果，只返回特定属性值
     * @param map
     * @param propertyNames
     * @return
     */
    T findOneByProperties(Map<String,Object> map,List<String> propertyNames);

    /**
     * 查询满足一个或多个相等条件的列表
     * @param map
     * @return
     */
    List<T> findByProperties(Map<String,Object> map);

    /**
     * 查询满足一个或多个相等条件的列表，只返回特定属性值
     * @param map
     * @param propertyNames
     * @return
     */
    List<T> findByProperties(Map<String,Object> map,List<String> propertyNames);

    /**
     *  查询满足一个或多个相等条件的分页结果
     * @param map
     * @param pageable
     * @return
     */
    Page<T> findByProperties(Map<String,Object> map, Pageable pageable);

    /**
     * 查询满足一个或多个相等条件的分页结果，只返回特定的属性值
     * @param map
     * @param pageable
     * @param propertyNames
     * @return
     */
    Page<T> findByProperties(Map<String,Object> map,Pageable pageable,List<String> propertyNames);

    /**
     * 通过一对相等的属性查询列表
     * @param name
     * @param value
     * @return
     */
    List<T> findByProperty(String name,Object value);

    /**
     * 通过一对相等的属性查询列表，只返回特定属性字段
     * @param name
     * @param value
     * @param propertyNames
     * @return
     */
    List<T> findByProperty(String name,Object value,List<String> propertyNames);

    /**
     * 查询全表数据
     * @return
     */
    List<T> findAll();

    /**
     * 查询全表数据，只返回特定属性值
     * @param propertyNames
     * @return
     */
    List<T> findAll(List<String> propertyNames);

    /**
     * 按分页返回列表数据
     * @param pageable
     * @return
     */
    Page<T> findAll(Pageable pageable);

    /**
     *  按分页返回列表数据，只返回特定列
     * @param pageable
     * @param propertyNames
     * @return
     */
    Page<T> findAll(Pageable pageable,List<String> propertyNames);

    /**
     * 根据specification条件 查询返回列表
     * @param specification
     * @return
     */
    List<T> findAll(Specification<T> specification);

    /**
     * 根据specification条件 查询返回列表，只返回特定列
     * @param specification
     * @param propertyNames
     * @return
     */
    List<T> findAll(Specification<T> specification, List<String> propertyNames);

    /**
     * 根据specification条件 查询返回分页
     * @param specification
     * @param pageable
     * @return
     */
    Page<T> findAll(Specification<T> specification,Pageable pageable);

    /**
     * 根据specification条件 查询返回分页，只返回特定列
     * @param specification
     * @param pageable
     * @param propertyNames
     * @return
     */
    Page<T> findAll(Specification<T> specification,Pageable pageable,List<String> propertyNames);

    /**
     * 查询列表，按特定排序
     * @param specification
     * @param sort
     * @return
     */
    List<T> findAll(Specification<T> specification, Sort sort);

    /**
     * 批量更新，按特定条件更新特定字段为特定值
     * @param specification
     * @param updatedNameValues
     * @return
     */
    Integer updateAll(Specification<T> specification,Map<String,Object> updatedNameValues);

    /**
     * 批量更新，某个字段等于某个值的，更新另一些字段值
     * @param fieldName
     * @param fieldValue
     * @param updatedNameValues
     * @return
     */
    Integer updateByProperty(String fieldName,Object fieldValue,Map<String,Object> updatedNameValues);

    /**
     * 批量更新，某些字段值过滤，更新另一些字段值
     * @param conditionMap
     * @param updatedNameValues
     * @return
     */
    Integer updateByProperties(Map<String,Object> conditionMap,Map<String,Object> updatedNameValues);

    /**
     * 批量删除，某些字段值
     * @param conditionMap
     * @return
     */
    Integer deleteByProperties(Map<String,Object> conditionMap);

    /**
     * 根据条件批量删除
     * @param specification
     * @return
     */
    Integer deleteAll(Specification<T> specification);

    /**
     *  执行本地sql返回 Query对象
     * @param sql
     * @return
     */
    Query getBySQL(String sql);

    /**
     * 执行hql 返回 Query对象
     * @param hql
     * @return
     */
    Query getByHQL(String hql);

    /**
     *  根据条件查询数量
     * @param specification
     * @return
     */
    Long getCount(Specification<T> specification);

}
