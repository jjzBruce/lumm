package com.lumm.cache.annotation.interceptor;


import cn.hutool.core.util.ServiceLoaderUtil;

import java.util.List;

/**
 * 所有{@link javax.interceptor.Interceptor @Interceptor}类都应该实现的标记接口。
 */
public interface Interceptor {

    /**
     * SPI加载 Interceptor 的实现，返回列表
     *
     * @return non-null
     */
    static List<Interceptor> loadInterceptors() {
        return ServiceLoaderUtil.loadList(Interceptor.class);
    }
}
