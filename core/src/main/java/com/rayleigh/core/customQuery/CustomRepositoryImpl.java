package com.rayleigh.core.customQuery;

import java.io.Serializable;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;


public class CustomRepositoryImpl <T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>  implements CustomRepository<T,ID> {
	
	private final EntityManager entityManager;
	
	public CustomRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	public List<Object[]> listBySQL(String sql) {
		return entityManager.createNativeQuery(sql).getResultList();
	}

	@Override
	public Query getBySQL(String sql) {
		return entityManager.createNativeQuery(sql);
	}

	@Override
	public Query getByHQL(String hql) {
		return entityManager.createQuery(hql);
	}

	@Transactional
	@Override
	public Integer updateAll(Specification<T> specification, Map<String, Object> needUpdatedNameValue) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<T> criteriaUpdate =criteriaBuilder.createCriteriaUpdate(this.getDomainClass());
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.getDomainClass());
		Root<T> root = criteriaUpdate.from(this.getDomainClass());
		if(null!=needUpdatedNameValue) {
			for (Map.Entry<String, Object> entry : needUpdatedNameValue.entrySet()) {
				criteriaUpdate.set(root.get(entry.getKey()), entry.getValue());
			}
			if(!needUpdatedNameValue.containsKey("updateDate")){
				criteriaUpdate.set(root.get("updateDate"), new Date());
			}
		}

		if(null!=specification) {
			criteriaUpdate.where(specification.toPredicate(root, criteriaQuery, criteriaBuilder));
		}
		Integer result =(Integer)entityManager.createQuery(criteriaUpdate).executeUpdate();
		return result;
	}

	@Transactional
	public Integer deleteAll(Specification<T> specification){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaDelete<T> criteriaDelete =criteriaBuilder.createCriteriaDelete(this.getDomainClass());
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.getDomainClass());
		Root<T> root = criteriaDelete.from(this.getDomainClass());
		if(null!=specification) {
			criteriaDelete.where(specification.toPredicate(root, criteriaQuery, criteriaBuilder));
		}
		Integer result = entityManager.createQuery(criteriaDelete).executeUpdate();
		return result;
	}

	@Override
	public Long getCount(Specification<T> specification) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<T> root = criteriaQuery.from(this.getDomainClass());
		if(null!=specification){
			criteriaQuery.where(specification.toPredicate(root, criteriaQuery, criteriaBuilder));
		}
		criteriaQuery.select(criteriaBuilder.count(root));
		criteriaQuery.orderBy(Collections.emptyList());
		return entityManager.createQuery(criteriaQuery).getSingleResult();
	}


}