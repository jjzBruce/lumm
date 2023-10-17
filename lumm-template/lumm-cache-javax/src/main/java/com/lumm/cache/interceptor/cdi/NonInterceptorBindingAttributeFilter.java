package com.lumm.cache.interceptor.cdi;


import com.lumm.cache.interceptor.InterceptorBindingAttributeFilter;
import com.lumm.cache.util.ClassLoaderUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class NonInterceptorBindingAttributeFilter implements InterceptorBindingAttributeFilter {

    private static final String NON_BINDING_ANNOTATION_CLASS_NAME = "javax.enterprise.util.Nonbinding";

    private static final ClassLoader classLoader = ClassLoaderUtils.getClassLoader(NonInterceptorBindingAttributeFilter.class);

    private static final boolean NON_BINDING_ANNOTATION_ABSENT = ClassUtils.isPresent(NON_BINDING_ANNOTATION_CLASS_NAME, classLoader);

    @Override
    public boolean accept(Method attributeMethod) {
        if (NON_BINDING_ANNOTATION_ABSENT) {
            Class<? extends Annotation> nonbindingClass = (Class<? extends Annotation>)
                    com.lumm.cache.util.ClassUtils.resolveClass(NON_BINDING_ANNOTATION_CLASS_NAME, classLoader);
            return !attributeMethod.isAnnotationPresent(nonbindingClass);
        }
        return true;
    }
}
