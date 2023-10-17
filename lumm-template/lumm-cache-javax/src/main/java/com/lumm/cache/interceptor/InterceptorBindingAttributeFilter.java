package com.lumm.cache.interceptor;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassLoaderUtil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.ServiceLoader;
import java.util.function.Predicate;

public interface InterceptorBindingAttributeFilter extends Predicate<Method> {

    default boolean test(Method attributeMethod) {
        return accept(attributeMethod);
    }

    boolean accept(Method attributeMethod);

    static Predicate<Method>[] filters() {
        ServiceLoader<InterceptorBindingAttributeFilter> load = ServiceLoader.load(InterceptorBindingAttributeFilter.class, ClassLoaderUtil.getClassLoader());
        return ArrayUtil.toArray((Collection)ListUtil.of(load), InterceptorBindingAttributeFilter.class);
    }

}
