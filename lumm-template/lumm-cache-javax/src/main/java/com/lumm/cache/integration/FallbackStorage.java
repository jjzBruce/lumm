package com.lumm.cache.integration;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import java.util.Comparator;

/**
 * 处理缓存的加载与写入，继承自 {@link CacheLoader} 和 {@link CacheWriter}
 */
public interface FallbackStorage<K, V> extends CacheLoader<K, V>, CacheWriter<K, V> {

    /**
     * 优先级比较
     */
    Comparator<FallbackStorage> PRIORITY_COMPARATOR = new PriorityComparator();

    /**
     * 获取当前的优先级
     *
     * @return 值越低，优先级越高
     */
    int getPriority();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 优先级比较器
     */
    class PriorityComparator implements Comparator<FallbackStorage> {

        @Override
        public int compare(FallbackStorage o1, FallbackStorage o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    }
}
