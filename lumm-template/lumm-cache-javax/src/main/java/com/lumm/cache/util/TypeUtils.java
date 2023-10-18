package com.lumm.cache.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Type工具类
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public abstract class TypeUtils {

    public static List<Class<?>> resolveTypeArguments(Class<?> targetClass) {
        List<Class<?>> classArguments = new ArrayList<>();
        Type[] typeArguments1 = TypeUtil.getTypeArguments(targetClass);
        if (ArrayUtil.isEmpty(typeArguments1)) {
            return Collections.emptyList();
        }
        for (Type type : typeArguments1) {
            classArguments.add(TypeUtil.getClass(type));
        }

        return classArguments;
    }

}
