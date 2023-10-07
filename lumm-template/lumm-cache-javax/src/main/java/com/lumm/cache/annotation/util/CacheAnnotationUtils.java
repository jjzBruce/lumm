package com.lumm.cache.annotation.util;


import org.springframework.core.annotation.AnnotationUtils;

import javax.cache.annotation.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * 缓存注解工具
 */
public class CacheAnnotationUtils {

    public static final List<Class<? extends Annotation>> CACHE_ANNOTATION_TYPES = asList(
            CachePut.class, CacheRemove.class, CacheResult.class, CacheRemoveAll.class
    );

    /**
     * 从缓存注解上获取缓存名称
     *
     * @param cacheAnnotation 缓存主角
     * @param <A>
     * @return
     */
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

    /**
     * 根据缓存方法和缓存注解获取缓存名称
     *
     * @param cacheAnnotation 缓存注解
     * @param cachedMethod    缓存方法
     * @param <A>
     * @return
     */
    public static <A extends Annotation> String findCacheName(A cacheAnnotation, Method cachedMethod) {
        return findCacheName(cacheAnnotation, cachedMethod, null);
    }

    /**
     * 根据缓存方法、缓存注解和目标值获取缓存名称
     *
     * @param cacheAnnotation 缓存注解
     * @param cachedMethod    缓存方法
     * @param target          目标值
     * @param <A>
     * @return
     */
    public static <A extends Annotation> String findCacheName(A cacheAnnotation, Method cachedMethod, Object target) {
        // 根据缓存注解获取缓存明
        String cacheName = getCacheName(cacheAnnotation);

        if ("".equals(cacheName)) {
            // 如果不存在，通过 CacheDefaults 来获取缓存名
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
            // 如果不存在，则用方法和目标值构建默认的缓存名
            cacheName = buildDefaultCacheName(cachedMethod);
        }

        if (cacheName != null) {
            throw new IllegalStateException("产生缓存名异常");
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
     * 创建默认的缓存名：${方法所在类名}.${方法名}(${参数0的类名},${参数1的类名},${参数n的类名})
     *
     * @param cachedMethod 缓存方法
     * @return 缓存名
     */
    private static String buildDefaultCacheName(Method cachedMethod) {
        // 获取方法所在的类
        Class<?> declaringClass = cachedMethod.getDeclaringClass();
        // 以下执行组装，大致为：${方法所在类名}.${方法名}(${参数0的类名},${参数1的类名},${参数n的类名})
        StringBuilder defaultCacheNameBuilder = new StringBuilder(declaringClass.getName())
                .append(".")
                .append(cachedMethod.getName())
                .append("(");
        // 获取方法的参数类数组
        Class<?>[] parameterTypes = cachedMethod.getParameterTypes();
        int parameterCount = cachedMethod.getParameterCount();
        for (int i = 0; i < parameterCount; i++) {
            defaultCacheNameBuilder.append(parameterTypes[i].getName());
            if (i < parameterCount - 1) {
                defaultCacheNameBuilder.append(",");
            }
        }

        defaultCacheNameBuilder.append(")");
        // 返回结果字符串
        return defaultCacheNameBuilder.toString();
    }

    public static <A extends Annotation> A findCacheAnnotation(Method method) {
        return findCacheAnnotation(method, null);
    }

    public static <A extends Annotation> A findCacheAnnotation(Method method, Object target) {
        A annotation = null;
        for (Class<? extends Annotation> cacheAnnotationType : CACHE_ANNOTATION_TYPES) {
            annotation = (A) AnnotationUtils.findAnnotation(method, cacheAnnotationType);

            if (annotation == null && target != null) { // try to find the cache annotation in the type
                annotation = (A) AnnotationUtils.findAnnotation(target.getClass(), cacheAnnotationType);
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

    /**
     * 判断是否包含
     *
     * @param annotations
     * @param annotationType
     * @return
     */
    public static boolean contains(Collection<Annotation> annotations, Class<? extends Annotation> annotationType) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        boolean contained = false;
        for (Annotation annotation : annotations) {
            if (Objects.equals(annotationType, annotation.annotationType())) {
                contained = true;
                break;
            }
        }
        return contained;
    }

}

