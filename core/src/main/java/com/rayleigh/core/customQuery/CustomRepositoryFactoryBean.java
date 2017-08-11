package com.rayleigh.core.customQuery;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
/**
 *开启自定义支持使用@EnableJpaRepositories的repositoryFactoryBeanClass来指定FacotryBean即可，代码如下：
 * @EnableJpaRepository(repositoryFactoryBeanClass=CustomRepositoryFactoryBean.class)
 */

public class CustomRepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID extends Serializable>
		extends JpaRepositoryFactoryBean<T, S, ID> {// 1  自定义RepositoryFacotry,继承JpaRepositoryFactoryBean

	public CustomRepositoryFactoryBean(Class<? extends T> repositoryInterface){
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {// 2  重写createRepositoryFactory方法，用当前的CustomerRepositoryFactory创建实例
		return new CustomRepositoryFactory(entityManager);
	}

	private static class CustomRepositoryFactory extends JpaRepositoryFactory {// 3 创建CustomerRepositoryFactory,并继承JpaRepositoryFactory


		public CustomRepositoryFactory(EntityManager entityManager) {
			super(entityManager);
		}

		@Override
		@SuppressWarnings({"unchecked"})
		protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(
				RepositoryInformation information, EntityManager entityManager) {// 4 重写getTargeRepository方法，获得当前自定义的Reposiotry实现
			return new CustomRepositoryImpl<T, ID>((Class<T>) information.getDomainType(), entityManager);

		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {// 5  重写getRepositoryBaseClass,获得当前自下定义的Reposiotry实现的类型
			return CustomRepositoryImpl.class;
		}
	}
}