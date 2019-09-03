package com.kevin.scepter.client.core.hotkey;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: kevin
 * @description: 标注方法参数名称
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:51
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    /**
     * 映射名称
     *
     * @return String
     */
    String value();

}
