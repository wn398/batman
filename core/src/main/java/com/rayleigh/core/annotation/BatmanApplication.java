package com.rayleigh.core.annotation;

import com.rayleigh.core.dynamicDataSource.EnableDynamicDataSource;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.*;
//可继承的自定义注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableCustomRepository
@EnableDynamicDataSource
@EnableAsync
@Inherited
public @interface BatmanApplication {
}
