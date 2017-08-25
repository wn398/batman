package com.rayleigh.core.dynamicDataSource;

import java.lang.annotation.*;

//存放数据源
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value();
}