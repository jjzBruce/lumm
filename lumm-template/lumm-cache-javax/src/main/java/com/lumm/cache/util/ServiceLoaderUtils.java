package com.lumm.cache.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ServiceLoaderUtil;


/**
 * ServiceLoader 工具类
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since
 */
public abstract class ServiceLoaderUtils {

    public static <T> T[] load(Class<T> clazz) {
        return ArrayUtil.toArray(ServiceLoaderUtil.loadList(clazz), clazz);
    }

}
