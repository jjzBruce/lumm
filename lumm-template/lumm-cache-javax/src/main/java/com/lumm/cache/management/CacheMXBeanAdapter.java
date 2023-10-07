package com.lumm.cache.management;

import javax.cache.configuration.CompleteConfiguration;
import javax.cache.management.CacheMXBean;

import static java.util.Objects.requireNonNull;

/**
 * {@link CacheMXBean} 适配器，基于 {@link CompleteConfiguration}
 */
class CacheMXBeanAdapter implements CacheMXBean {

    private final CompleteConfiguration<?, ?> configuration;

    public CacheMXBeanAdapter(CompleteConfiguration<?, ?> configuration) throws NullPointerException {
        requireNonNull(configuration, "参数 'configuration' 不能为 null!");
        this.configuration = configuration;
    }

    @Override
    public String getKeyType() {
        return configuration.getKeyType().getName();
    }

    @Override
    public String getValueType() {
        return configuration.getValueType().getName();
    }

    @Override
    public boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    @Override
    public boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }

    @Override
    public boolean isStoreByValue() {
        return configuration.isStoreByValue();
    }

    @Override
    public boolean isStatisticsEnabled() {
        return configuration.isStatisticsEnabled();
    }

    @Override
    public boolean isManagementEnabled() {
        return configuration.isManagementEnabled();
    }
}
