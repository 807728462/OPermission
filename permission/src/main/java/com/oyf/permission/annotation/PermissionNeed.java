package com.oyf.permission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @创建者 oyf
 * @创建时间 2019/12/5 16:57
 * @描述  权限申请
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionNeed {
    String[] permissions();//可以同时申请多个权限

    int requestCode() default 0;//请求的code
}
