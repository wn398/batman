package com.rayleigh.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FieldInfo {
    String value() default "";
}