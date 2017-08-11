package com.rayleigh.core.annotation;

import com.rayleigh.core.customQuery.CustomRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableJpaAuditing
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomRepositoryFactoryBean.class)//在配置类上配置@EnableJpaRepositories,并指定repositoryFactoryBeanClass,让我们自定义的Repository实现起效
public @interface EnableCustomRepository {
}
