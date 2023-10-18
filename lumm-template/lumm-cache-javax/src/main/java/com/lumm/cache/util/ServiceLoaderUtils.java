package com.lumm.cache.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ServiceLoaderUtil;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ServiceLoader 工具类
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since
 */
public abstract class ServiceLoaderUtils {

    private static final Map<ClassLoader, Map<Class<?>, ServiceLoader<?>>> serviceLoadersCache = new ConcurrentHashMap<>();

    public static <T> T[] load(Class<T> clazz) {
        return ArrayUtil.toArray(ServiceLoaderUtil.loadList(clazz), clazz);
    }

    public static <T> T loadSpi(Class<T> serviceClass, ClassLoader classLoader) {
        return load(serviceClass, classLoader).iterator().next();
    }

    public static <T> ServiceLoader<T> load(Class<T> serviceClass, ClassLoader classLoader) {
        Map<Class<?>, ServiceLoader<?>> serviceLoadersMap = serviceLoadersCache.computeIfAbsent(classLoader, cl -> new ConcurrentHashMap<>());
        ServiceLoader<T> serviceLoader = (ServiceLoader<T>) serviceLoadersMap.computeIfAbsent(serviceClass,
                service -> ServiceLoader.load(service, classLoader));
        return serviceLoader;
    }
    
}
