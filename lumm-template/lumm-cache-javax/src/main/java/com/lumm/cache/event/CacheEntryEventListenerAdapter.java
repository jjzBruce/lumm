package com.lumm.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * {@link ConditionalCacheEntryEventListener} 实现类，适配Javax Cache框架中的监听器
 *
 * @see CacheEntryListenerConfiguration
 */
public class CacheEntryEventListenerAdapter<K, V> implements ConditionalCacheEntryEventListener<K, V> {

    private static List<Object> eventTypesAndHandleMethodNames = Arrays.asList(
            EventType.CREATED, "onCreated",
            EventType.UPDATED, "onUpdated",
            EventType.EXPIRED, "onExpired",
            EventType.REMOVED, "onRemoved"
    );

    private final CacheEntryListenerConfiguration<K, V> configuration;

    private final CacheEntryEventFilter<? super K, ? super V> cacheEntryEventFilter;

    private final CacheEntryListener<? super K, ? super V> cacheEntryListener;

    private final Map<EventType, Method> eventTypeMethods;

    private final Executor executor;

    /**
     * 构造
     *
     * @param configuration
     */
    public CacheEntryEventListenerAdapter(CacheEntryListenerConfiguration<K, V> configuration) {
        this.configuration = configuration;
        this.cacheEntryEventFilter = getCacheEntryEventFilter(configuration);
        this.cacheEntryListener = configuration.getCacheEntryListenerFactory().create();
        this.eventTypeMethods = determineEventTypeMethods(cacheEntryListener);
        this.executor = getExecutor(configuration);
    }

    @Override
    public boolean supports(CacheEntryEvent<? extends K, ? extends V> event) {
        // 事件类型匹配 以及 事件过滤器满足此条件
        return supportsEventType(event) && cacheEntryEventFilter.evaluate(event);
    }

    private boolean supportsEventType(CacheEntryEvent<? extends K, ? extends V> event) {
        return getSupportedEventTypes().contains(event.getEventType());
    }

    @Override
    public void onEvent(CacheEntryEvent<? extends K, ? extends V> event) {
        if (!supports(event)) {
            return;
        }

        EventType eventType = event.getEventType();
        Method handleMethod = eventTypeMethods.get(eventType);

        executor.execute(() -> {
            try {
                handleMethod.invoke(cacheEntryListener, Collections.singleton(event));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new CacheEntryListenerException(e);
            }
        });

    }

    @Override
    public Set<EventType> getSupportedEventTypes() {
        return eventTypeMethods.keySet();
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public int hashCode() {
        return configuration.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CacheEntryEventListenerAdapter)) {
            return false;
        }
        CacheEntryEventListenerAdapter another = (CacheEntryEventListenerAdapter) object;
        return this.configuration.equals(another.configuration);
    }

    /**
     * 根据配置获取缓存事件过滤器
     *
     * @param configuration 配置
     * @return CacheEntryEventFilter<? super K, ? super V>
     */
    private CacheEntryEventFilter<? super K, ? super V> getCacheEntryEventFilter(CacheEntryListenerConfiguration<K, V> configuration) {
        Factory<CacheEntryEventFilter<? super K, ? super V>> factory = configuration.getCacheEntryEventFilterFactory();
        CacheEntryEventFilter<? super K, ? super V> filter = null;

        if (factory != null) {
            filter = factory.create();
        }

        if (filter == null) {
            // 默认全部放行
            filter = e -> true;
        }

        return filter;
    }

    /**
     * 确定缓存事件类型方法
     *
     * @param cacheEntryListener
     * @return
     */
    private Map<EventType, Method> determineEventTypeMethods(CacheEntryListener<? super K, ? super V> cacheEntryListener) {
        Map<EventType, Method> eventTypeMethods = new HashMap<>(EventType.values().length);
        Class<?> cacheEntryListenerClass = cacheEntryListener.getClass();
        for (int i = 0; i < eventTypesAndHandleMethodNames.size(); ) {
            EventType eventType = (EventType) eventTypesAndHandleMethodNames.get(i++);
            String handleMethodName = (String) eventTypesAndHandleMethodNames.get(i++);
            try {
                // 利用反射确定监听回调方法
                Method handleMethod = cacheEntryListenerClass.getMethod(handleMethodName, Iterable.class);
                if (handleMethod != null) {
                    eventTypeMethods.put(eventType, handleMethod);
                }
            } catch (NoSuchMethodException ignored) {
            }

        }
        return Collections.unmodifiableMap(eventTypeMethods);
    }

    private Executor getExecutor(CacheEntryListenerConfiguration<K, V> configuration) {
        Executor executor;
        if (configuration.isSynchronous()) {
            // 配置是同步，不需要多线程多线程
            executor = Runnable::run;
        } else {
            // 配置是异步，使用 ForkJoinPool 的线程池
            executor = ForkJoinPool.commonPool();
        }
        return executor;
    }
}
