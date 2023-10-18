package com.lumm.cache.interceptor;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 日志注解
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, TYPE})
@InterceptorBinding
@Inherited
public @interface Logging {
    
    @Nonbinding
    String name() default "ROOT";
    
}
