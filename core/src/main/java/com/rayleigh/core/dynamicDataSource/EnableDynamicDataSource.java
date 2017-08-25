package com.rayleigh.core.dynamicDataSource;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//封装动态数据源是否开启
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({DynamicDataSourceRegister.class})
public @interface EnableDynamicDataSource {
}
