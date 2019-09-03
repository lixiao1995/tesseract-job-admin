package com.kevin.scepter.client.core.hotkey;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: kevin
 * @description: 代理
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-07 10:38
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProxyModule {

    String value() default "";

    Class<?> from() default void.class;

    boolean singleton() default true;
}
