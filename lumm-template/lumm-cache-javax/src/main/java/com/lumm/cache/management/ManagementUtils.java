package com.lumm.cache.management;

import javax.cache.Cache;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.management.CacheMXBean;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

/**
 * 缓存JMX工具
 */
public abstract class ManagementUtils {

    /**
     * 根据缓存配置创建缓存JMX适配器
     * @param configuration 缓存配置
     * @return CacheMXBean
     */
    public static CacheMXBean adaptCacheMXBean(CompleteConfiguration<?, ?> configuration) {
        return new CacheMXBeanAdapter(configuration);
    }

    private static ObjectName createObjectName(Cache<?, ?> cache, String type) {
        Hashtable<String, String> props = new Hashtable<>();
        props.put("type", type);
        props.put("name", cache.getName());
        props.put("uri", getUri(cache));
        ObjectName objectName ;
        try {
            objectName = new ObjectName("javax.cache", props);
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
        return objectName;
    }

    private static String getUri(Cache<?, ?> cache) {
        URI uri = cache.getCacheManager().getURI();
        try {
            return URLEncoder.encode(uri.toASCIIString(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerMBeansIfRequired(Cache<?, ?> cache, CacheStatistics cacheStatistics) {
        CompleteConfiguration configuration = cache.getConfiguration(CompleteConfiguration.class);
        if (configuration.isManagementEnabled()) {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            registerCacheMXBeanIfRequired(cache, configuration, mBeanServer);
            registerCacheStatisticsMXBeanIfRequired(cache, configuration, mBeanServer, cacheStatistics);
        }
    }

    private static void registerCacheStatisticsMXBeanIfRequired(Cache<?, ?> cache, CompleteConfiguration configuration,
                                                                MBeanServer mBeanServer, CacheStatistics cacheStatistics) {
        if (configuration.isStatisticsEnabled()) {
            ObjectName objectName = createObjectName(cache, "CacheStatistics");
            registerMBean(objectName, cacheStatistics, mBeanServer);
        }
    }

    private static void registerCacheMXBeanIfRequired(Cache<?, ?> cache, CompleteConfiguration configuration, MBeanServer mBeanServer) {
        ObjectName objectName = createObjectName(cache, "CacheConfiguration");
        registerMBean(objectName, adaptCacheMXBean(configuration), mBeanServer);
    }

    private static void registerMBean(ObjectName objectName, Object object, MBeanServer mBeanServer) {
        try {
            if (!mBeanServer.isRegistered(objectName)) {
                mBeanServer.registerMBean(object, objectName);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
