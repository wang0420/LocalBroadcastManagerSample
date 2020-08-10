package com.wang.broadcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wnagwei
 * @date 2019/5/18
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Action {
    String[] value();
}