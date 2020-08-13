package com.wang.broadcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解默认是RetentionPolicy.CLASS，METHOD:用于描述方法
 * version 1.0.0
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Action {
    String[] value();
}