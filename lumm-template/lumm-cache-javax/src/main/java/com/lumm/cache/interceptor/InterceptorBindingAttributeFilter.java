package com.lumm.cache.interceptor;

import com.lumm.cache.util.ServiceLoaderUtils;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public interface InterceptorBindingAttributeFilter extends Predicate<Method> {

    default boolean test(Method attributeMethod) {
        return accept(attributeMethod);
    }

    boolean accept(Method attributeMethod);

    static Predicate<Method>[] filters() {
        return ServiceLoaderUtils.load(InterceptorBindingAttributeFilter.class);
    }

}
