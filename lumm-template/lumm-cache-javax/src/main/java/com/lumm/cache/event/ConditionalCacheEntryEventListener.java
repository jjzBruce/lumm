package com.lumm.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.event.*;
import java.util.EventListener;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 缓存事件{@link CacheEntryEvent}有条件的监听器{@link EventListener}接口
 *
 * @param <K> 缓存键类泛型
 * @param <V> 缓存值类泛型
 * @see CacheEntryListener
 * @see CacheEntryEventFilter
 * @see CacheEntryListenerConfiguration
 */
public interface ConditionalCacheEntryEventListener<K, V> extends EventListener {

    /**
     * 是否支持
     *
     * @param event 缓存事件
     * @return 支持该缓存事件的监听器将会被触发
     * @throws CacheEntryListenerException
     * @see CacheEntryEventFilter#evaluate(CacheEntryEvent)
     */
    boolean supports(CacheEntryEvent<? extends K, ? extends V> event) throws CacheEntryListenerException;

    /**
     * 监听缓存事件触发
     *
     * @param event 缓存事件
     * @see CacheEntryCreatedListener
     * @see CacheEntryUpdatedListener
     * @see CacheEntryRemovedListener
     * @see CacheEntryExpiredListener
     */
    void onEvent(CacheEntryEvent<? extends K, ? extends V> event);

    /**
     * 监听缓存多个事件触发
     *
     * @param events one or more events
     * @see CacheEntryCreatedListener
     * @see CacheEntryUpdatedListener
     * @see CacheEntryRemovedListener
     * @see CacheEntryExpiredListener
     */
    default void onEvents(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {
        events.forEach(this::onEvent);
    }

    /**
     * 获取支持的事件类型
     *
     * @return non-null
     */
    Set<EventType> getSupportedEventTypes();

    /**
     * 并发调度器
     *
     * @return non-null
     * @see CacheEntryListenerConfiguration#isSynchronous()
     */
    Executor getExecutor();

    @Override
    int hashCode();

    @Override
    boolean equals(Object object);
}
