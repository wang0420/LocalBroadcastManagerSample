package com.wang.broadcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1.0.2
 *
 * @author wangwei
 * @date 2019/5/18
 * @Retention(RetentionPolicy.CLASS) 自定义注解默认是RetentionPolicy.CLASS，
 * 看的出来Java是推荐把注解放到.class中去的。
 * @Target(ElementType.METHOD) METHOD:用于描述方法
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Action {
    String[] value();
}