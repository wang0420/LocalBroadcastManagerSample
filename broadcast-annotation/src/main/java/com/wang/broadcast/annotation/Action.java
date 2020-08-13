package com.wang.broadcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 2019/5/18
 * (RetentionPolicy.CLASS) 自定义注解默认是RetentionPolicy.CLASS，
 * 看的出来Java是推荐把注解放到.class中去的。
 * METHOD:用于描述方法
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Action {
    String[] value();
}