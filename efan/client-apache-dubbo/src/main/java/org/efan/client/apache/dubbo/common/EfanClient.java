package org.efan.client.apache.dubbo.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *　　　　　　__
 *  　___  / _|  __ _  _ __
 * 　/ _ \| |_  / _` || '_ \
 *　|  __/|  _|| (_| || | | |
 * 　\___||_|   \__,_||_| |_|
 * @author  fan
 * @date  2020-3-20 16:01
 * @description: TODO : 方法注解，用于配置接口地址和描述
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface EfanClient {

    /**
     * 提供出去的接口路径
     * Path string.
     *
     * @return the string
     */
    String path();

    /**
     * 接口路径描述,方便用户选择.
     *
     * @return String
     */
    String desc();

    /**
     * Enabled boolean.
     *
     * @return the boolean
     */
    boolean enabled() default true;
}
