package com.lumm.cache.interceptor.util;


import cn.hutool.core.annotation.AnnotationUtil;
import com.lumm.cache.util.AnnotationUtils;

import javax.cache.annotation.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * The utilities class for Java Cache {@link Annotation}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class CacheAnnotationUtils {

    public static final List<Class<? extends Annotation>> CACHE_ANNOTATION_TYPES = asList(
            CachePut.class, CacheRemove.class, CacheResult.class, CacheRemoveAll.class
    );

    private static <A extends Annotation> String getCacheName(A cacheAnnotation) {
        String cacheName = null;
        if (cacheAnnotation instanceof CachePut) {
            CachePut cachePut = (CachePut) cacheAnnotation;
            cacheName = cachePut.cacheName();
        } else if (cacheAnnotation instanceof CacheRemove) {
            CacheRemove cacheRemove = (CacheRemove) cacheAnnotation;
            cacheName = cacheRemove.cacheName();
        } else if (cacheAnnotation instanceof CacheRemoveAll) {
            CacheRemoveAll cacheRemoveAll = (CacheRemoveAll) cacheAnnotation;
            cacheName = cacheRemoveAll.cacheName();
        } else if (cacheAnnotation instanceof CacheResult) {
            CacheResult cacheResult = (CacheResult) cacheAnnotation;
            cacheName = cacheResult.cacheName();
        } else if (cacheAnnotation instanceof CacheDefaults) {
            CacheDefaults cacheDefaults = (CacheDefaults) cacheAnnotation;
            cacheName = cacheDefaults.cacheName();
        }
        return cacheName;
    }

    public static <A extends Annotation> String findCacheName(A cacheAnnotation, Method cachedMethod) {
        return findCacheName(cacheAnnotation, cachedMethod, null);
    }

    public static <A extends Annotation> String findCacheName(A cacheAnnotation, Method cachedMethod, Object target) {
        String cacheName = getCacheName(cacheAnnotation);

        if ("".equals(cacheName)) { // default value or annotation is absent
            Class<?> declaringClass = cachedMethod.getDeclaringClass();
            CacheDefaults cacheDefaults = declaringClass.getAnnotation(CacheDefaults.class);
            if (cacheDefaults == null && target != null) {
                cacheDefaults = target.getClass().getAnnotation(CacheDefaults.class);
            }
            if (cacheDefaults != null) {
                cacheName = cacheDefaults.cacheName();
            }
        }

        if ("".equals(cacheName)) {
            cacheName = buildDefaultCacheName(cachedMethod);
        }

        if (cacheName != null) {

        }

        return cacheName;
    }

    public static CacheDefaults findCacheDefaults(Method cachedMethod, Object target) {
        Class<?> declaringClass = cachedMethod.getDeclaringClass();
        CacheDefaults cacheDefaults = declaringClass.getAnnotation(CacheDefaults.class);
        if (cacheDefaults == null && target != null) {
            cacheDefaults = target.getClass().getAnnotation(CacheDefaults.class);
        }
        return cacheDefaults;
    }

    /**
     * defaults to: package.name.ClassName.methodName(package.ParameterType,package.ParameterType)
     *
     * @param cachedMethod the method to be cached
     * @return package.name.ClassName.methodName(package.ParameterType, package.ParameterType)
     */
    private static String buildDefaultCacheName(Method cachedMethod) {
        Class<?> declaringClass = cachedMethod.getDeclaringClass();
        StringBuilder defaultCacheNameBuilder = new StringBuilder(declaringClass.getName())
                .append(".")
                .append(cachedMethod.getName())
                .append("(");

        Class<?>[] parameterTypes = cachedMethod.getParameterTypes();
        int parameterCount = cachedMethod.getParameterCount();
        for (int i = 0; i < parameterCount; i++) {
            defaultCacheNameBuilder.append(parameterTypes[i].getName());
            if (i < parameterCount - 1) {
                defaultCacheNameBuilder.append(",");
            }
        }

        defaultCacheNameBuilder.append(")");
        return defaultCacheNameBuilder.toString();
    }

    public static <A extends Annotation> A findCacheAnnotation(Method method) {
        return findCacheAnnotation(method, null);
    }

    public static <A extends Annotation> A findCacheAnnotation(Method method, Object target) {
        A annotation = null;
        for (Class<? extends Annotation> cacheAnnotationType : CACHE_ANNOTATION_TYPES) {
            annotation = (A) AnnotationUtil.getAnnotation(method, cacheAnnotationType);

            if (annotation == null && target != null) { // try to find the cache annotation in the type
                annotation = (A) AnnotationUtil.getAnnotation(target.getClass(), cacheAnnotationType);
            }

            if (annotation != null) {
                break;
            }
        }

        return annotation;
    }

    private static <A extends Annotation> void assertCacheAnnotationType(Class<A> cacheAnnotationType) {
        if (!CACHE_ANNOTATION_TYPES.contains(cacheAnnotationType)) {
            throw new IllegalArgumentException("The 'cacheAnnotationType' argument must be on of " + CACHE_ANNOTATION_TYPES);
        }
    }

}

