package com.lumm.cache.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 缓存配置{@link CacheConfiguration}实现类，基于{@link Properties}
 *
 */
public class PropertiesCacheConfiguration implements CacheConfiguration {

    private final Map<String, String> config;

    public PropertiesCacheConfiguration(Properties properties) {
        this.config = new HashMap<>(properties.size());
        for (String propertyName : properties.stringPropertyNames()) {
            config.put(propertyName, properties.getProperty(propertyName));
        }
    }

    @Override
    public String getProperty(String propertyName) {
        return config.get(propertyName);
    }
}
