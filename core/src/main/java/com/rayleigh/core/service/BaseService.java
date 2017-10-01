package com.rayleigh.core.service;

import com.rayleigh.core.customQuery.CustomRepository;
import com.rayleigh.core.model.BaseModel;
import com.rayleigh.core.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Created by wangn20 on 2017/6/29.
 */
public interface BaseService<T extends BaseModel,R extends CustomRepository>{
    Logger logger = LoggerFactory.getLogger(BaseService.class);
//     R getRepository();
//
//    default Collection<T> saveOrUpdate(Collection<T> list){
//         return getRepository().save(list);
//     }
//
//    default T saveOrUpdate(T t){
//        return (T)getRepository().save(t);
//    }
//
//    default void delete(Collection<String> ids){
//        getRepository().delete(ids);
//    }
//
//    default void delete(String id){
//        getRepository().delete(id);
//    }
//
//    default List<T> findByIds(Collection<String> ids){
//        return getRepository().findAll(ids);
//    }
//
//    default T findOne(String id){
//        return (T)getRepository().findOne(id);
//    }
//
//    default  List<Object[]> listBySQL(String sql){
//        return getRepository().listBySQL(sql);
//    }
//
//    default Page<T> findByAuto(T t, Pageable pageable){
//        return getRepository().findByAuto(t,pageable);
//    }
//
//    default Page<T> findAll(Pageable pageable){
//        return getRepository().findAll(pageable);
//    }
//
//    default List<T> findAll(Specification<T> specification){
//        return getRepository().findAll(specification);
//    }
//
//    default Page<T> findAll(Specification<T> specification,Pageable pageable){
//        return getRepository().findAll(specification,pageable);
//    }
//
//    default List<T> findAll(){
//        return getRepository().findAll();
//    }


}
