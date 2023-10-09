package com.lumm.cache.interceptor;


import com.lumm.cache.util.ServiceLoaderUtils;


/**
 * 所有{@link javax.interceptor.Interceptor @Interceptor}类都应该实现的标记接口。
 */
public interface Interceptor {

    /**
     * SPI加载 Interceptor 的实现，返回列表
     *
     * @return non-null
     */
    static Interceptor[] loadInterceptors() {
        return ServiceLoaderUtils.load(Interceptor.class);
    }
}
