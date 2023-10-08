package com.lumm.cache.configuration;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;

import javax.cache.Cache;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 缓存配置
 *
 * @see CompleteConfiguration
 */
public interface CacheConfiguration extends CompleteConfiguration {

    /**
     * 配置项：缓存前缀
     *
     * @see Cache
     */
    String CACHE_PROPERTY_PREFIX = "javax.cache.Cache.";

    /**
     * 配置项：缓存键类型名称配置 {@link Configuration#getKeyType()}
     */
    String CACHE_KEY_TYPE_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "key-type";

    /**
     * 配置项：缓存值类型名称配置 {@link Configuration#getKeyType()}
     */
    String CACHE_VALUE_TYPE_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "value-type";

    /**
     * 配置项：是否存储值的副本 {@link Configuration#isStoreByValue()}<br/>
     * <ul>
     *     <li>true: 那么缓存将存储数据的副本。这意味着当你从缓存中获取数据时，将返回一个数据的深拷贝，而不是原始数据的引用。</li>
     *     <li>false: 那么缓存将存储数据的引用。这意味着当你从缓存中获取数据时，将返回原始数据的引用。</li>
     * </ul>
     */
    String STORE_BY_VALUE_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "store-by-value";

    /**
     * 配置项：是否读穿透 {@link CompleteConfiguration#isReadThrough()} <br/>
     * <ul>
     *     <li>true: 启用了读穿透缓存功能。这意味着当尝试从缓存中获取某个键对应的值时，如果该值不存在于缓存中，缓存将尝试从指定的数据源加载该值</li>
     *     <li>false: 禁用了读穿透缓存功能。这意味着当尝试从缓存中获取某个键对应的值时，如果该值不存在于缓存中，缓存将不会自动从数据源加载，而是返回 null 或其他默认值。</li>
     * </ul>
     */
    String READ_THROUGH_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "read-through";

    /**
     * 配置项：是否写穿透 {@link CompleteConfiguration#isWriteThrough()} <br/>
     * <ul>
     *     <li>true: 启用了写穿透缓存功能。这意味着当应用程序尝试将数据写入缓存时，缓存会自动将相同的数据写入到指定的数据源中，以确保数据在缓存和数据源之间的一致性。</li>
     *     <li>false: 禁用了写穿透缓存功能。在这种情况下，当应用程序尝试写入缓存时，缓存将仅在缓存中更新数据，而不会自动将数据写入到数据源中。</li>
     * </ul>
     */
    String WRITE_THROUGH_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "write-through";

    /**
     * 配置项：是否启用缓存统计信息的收集和报告 {@link CompleteConfiguration#isStatisticsEnabled()} <br/>
     * <ul>
     *     <li>true: 启用了缓存的统计信息收集功能。这意味着缓存会自动跟踪各种性能指标，并将其记录下来。</li>
     *     <li>false: 禁用了缓存的统计信息收集功能。在这种情况下，缓存将不会跟踪或报告性能指标，这可能会使你无法有效地监视和调优缓存的性能。</li>
     * </ul>
     */
    String STATISTICS_ENABLED_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "statistics-enabled";

    /**
     * 配置项：是否启用缓存的管理功能 {@link CompleteConfiguration#isManagementEnabled()} <br/>
     * <ul>
     *     <li>true: 启用了缓存的管理功能。这意味着缓存可以被管理工具或API所监视，以提供有关缓存的运行时信息，如缓存的状态、统计数据、配置等。</li>
     *     <li>false: 禁用了缓存的管理功能。在这种情况下，缓存将不会提供管理信息，这可能会限制你对缓存的监视和管理能力。</li>
     * </ul>
     */
    String MANAGEMENT_ENABLED_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "management-enabled";

    /**
     * 配置项：缓存的条目监听器配置 {@link CompleteConfiguration#getCacheEntryListenerConfigurations()} <br/>
     */
    String ENTRY_LISTENER_CONFIGURATIONS_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "entry-listener-configurations";

    /**
     * 配置项：缓存数据加载工厂 {@link CompleteConfiguration#getCacheLoaderFactory()} <br/>
     */
    String CACHE_LOADER_FACTORY_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "loader.factory";

    /**
     * 配置项：缓存数据写入工厂 {@link CompleteConfiguration#getCacheWriterFactory()} <br/>
     */
    String CACHE_WRITER_FACTORY_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "writer.factory";

    /**
     * 配置项：缓存数据过期工厂 {@link CompleteConfiguration#getExpiryPolicyFactory()}
     */
    String EXPIRY_POLICY_FACTORY_PROPERTY_NAME = CACHE_PROPERTY_PREFIX + "expiry-policy.factory";

    /**
     * 获取配置名
     *
     * @param propertyName 配置名
     * @return <code>null</code> if not found
     */
    String getProperty(String propertyName);

    /**
     * 获取配置值，如果不存在则使用默认值
     *
     * @param propertyName 配置名
     * @param defaultValue 配置默认值
     * @return <code>defaultValue</code> if not found
     */
    default String getProperty(String propertyName, String defaultValue) {
        String propertyValue = getProperty(propertyName);
        return propertyValue == null ? defaultValue : propertyValue;
    }

    /**
     * 获取配置值
     *
     * @param propertyName 配置名
     * @param propertyType 配置值类型
     * @return <code>null</code> if not found
     */
    default <T> T getProperty(String propertyName, Class<T> propertyType) {
        String propertyValue = getProperty(propertyName);
        return Convert.convert(propertyType, propertyValue);
    }

    /**
     * 获取配置值，如果不存在则使用默认值
     *
     * @param propertyName 配置名
     * @param propertyType 配置值类型
     * @param defaultValue 默认值
     * @return <code>null</code> if not found
     */
    default <T> T getProperty(String propertyName, Class<T> propertyType, T defaultValue) {
        T propertyValue = getProperty(propertyName, propertyType);
        return propertyValue == null ? defaultValue : propertyValue;
    }

    @Override
    default Class<?> getKeyType() {
        return getProperty(CACHE_KEY_TYPE_PROPERTY_NAME, Class.class, Object.class);
    }

    @Override
    default Class<?> getValueType() {
        return getProperty(CACHE_VALUE_TYPE_PROPERTY_NAME, Class.class, Object.class);
    }

    @Override
    default boolean isStoreByValue() {
        return getProperty(STORE_BY_VALUE_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isReadThrough() {
        return getProperty(READ_THROUGH_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isWriteThrough() {
        return getProperty(WRITE_THROUGH_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isStatisticsEnabled() {
        return getProperty(STATISTICS_ENABLED_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default boolean isManagementEnabled() {
        return getProperty(MANAGEMENT_ENABLED_PROPERTY_NAME, Boolean.class, Boolean.TRUE);
    }

    @Override
    default Iterable<CacheEntryListenerConfiguration> getCacheEntryListenerConfigurations() {
        String propertyValue = getProperty(ENTRY_LISTENER_CONFIGURATIONS_PROPERTY_NAME);
        List<String> propertyValues = StrUtil.split(propertyValue, ',');
        List<Class<? extends CacheEntryListenerConfiguration>> configurationClasses = new LinkedList<>();
        propertyValues.forEach(p -> {
            configurationClasses.add(Convert.convert(Class.class, p));
        });
        if (CollUtil.isEmpty(configurationClasses)) {
            return Collections.emptyList();
        } else {
            return configurationClasses.stream()
                    .map(this::unwrap)
                    .collect(Collectors.toList());
        }
    }

    @Override
    default Factory<CacheLoader> getCacheLoaderFactory() {
        Class<Factory<CacheLoader>> factoryClass = getProperty(CACHE_LOADER_FACTORY_PROPERTY_NAME, Class.class);
        return factoryClass == null ? null : unwrap(factoryClass);
    }

    @Override
    default Factory<CacheWriter> getCacheWriterFactory() {
        Class<Factory<CacheWriter>> factoryClass = getProperty(CACHE_WRITER_FACTORY_PROPERTY_NAME, Class.class);
        return factoryClass == null ? null : unwrap(factoryClass);
    }

    @Override
    default Factory<ExpiryPolicy> getExpiryPolicyFactory() {
        Class<Factory<ExpiryPolicy>> factoryClass = getProperty(EXPIRY_POLICY_FACTORY_PROPERTY_NAME, Class.class);
        return factoryClass == null ? null : unwrap(factoryClass);
    }

    default <T> T unwrap(Class<T> clazz) {
        T value = null;
        try {
            value = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }
}
