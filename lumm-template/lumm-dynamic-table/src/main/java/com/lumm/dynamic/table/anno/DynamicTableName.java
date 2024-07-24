package com.lumm.dynamic.table.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 动态表名
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 1.0.0
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface DynamicTableName {

    /**
     * 参数表达式 <br/>
     * 内置参数:
     *    tableName： 当前表名
     *    tenant：    当前租户Id的下标排序 aaa - zzz
     */
    String dynamicExpression() default "";

}
