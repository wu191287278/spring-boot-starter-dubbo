package com.alibaba.boot.dubbo;

import com.alibaba.dubbo.config.annotation.Reference;

import java.lang.annotation.*;

/**
 * Created by wuyu on 2016/10/31.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DubboClient {

    //Dubbo @Reference bug
    String protocol() default "";

    Reference value() default @Reference;
}
