//package com.lumm.cache;
//
//import org.springframework.cache.support.AbstractCacheManager;
//
//import javax.cache.CacheManager;
//import javax.cache.Caching;
//import javax.cache.configuration.OptionalFeature;
//import javax.cache.spi.CachingProvider;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.lang.reflect.Constructor;
//import java.net.URI;
//import java.net.URL;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.function.Supplier;
//
//import static java.lang.String.format;
//
///**
// * @Description 可以被配置的Cache提供方
// * @Author zhangj
// * @Date 2022/11/27 22:49
// * @see Caching#getCachingProvider()
// * @see AbstractCacheManager
// */
//public class ConfigurableCachingProvider implements CachingProvider {
//
//    /**
//     * {@link #getDefaultProperties() 默认配置}
//     */
//    public static final String DEFAULT_PROPERTIES_RESOURCE_NAME =
//            "meta-inf/default-caching-provider.properties";
//
//    public static final String DEFAULT_PROPERTIES_PRIORITY_PROPERTY_NAME =
//            "javax.cache.spi.CachingProvider.default-properties.priority";
//
//    public static final String DEFAULT_URI_PROPERTY_NAME = "javax.cache.spi.CachingProvider.default-uri";
//
//    public static final String DEFAULT_URI_DEFAULT_PROPERTY_VALUE = "in-memory://localhost/";
//
//    /**
//     * {@link CacheManager}的映射前缀 <br/>
//     * javax.cache.CacheManager.mappings.${uri.scheme}=com.acom.SomeSchemeCacheManager
//     */
//    public static final String CACHE_MANAGER_MAPPING_PROPERTY_PREFIX = "javax.cache.CacheManager.mappings.";
//
//    /**
//     * 文件默认编码
//     */
//    public static final String DEFAULT_ENCODING = System.getProperty("file.encoding", "UTF-8");
//
//    /**
//     * 默认的内存缓存实现
//     */
//    public static final URI DEFAULT_URI = URI.create("in-memory://localhost/");
//
//    /**
//     * 默认配置
//     */
//    private Properties defaultProperties;
//
//    private URI defaultURI;
//
//    /**
//     * {@link CacheManager} 仓库
//     */
//    private ConcurrentMap<String, CacheManager> cacheManagerRepository = new ConcurrentHashMap<>();
//
//
//    /**
//     * {@inheritDoc}
//     *
//     * @return 使用 {@link Caching} 的ClassLoader，如果为null，则获取当前线程的ClassLoader
//     */
//    @Override
//    public ClassLoader getDefaultClassLoader() {
//        ClassLoader classLoader = Caching.getDefaultClassLoader();
//        return classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
//    }
//
//    @Override
//    public URI getDefaultURI() {
//        if (defaultURI == null) {
//            String defaultURIValue = getDefaultProperties().getProperty(DEFAULT_URI_PROPERTY_NAME, DEFAULT_URI_DEFAULT_PROPERTY_VALUE);
//            defaultURI = URI.create(defaultURIValue);
//        }
//        return defaultURI;
//    }
//
//    @Override
//    public Properties getDefaultProperties() {
//        if (defaultProperties == null) {
//            defaultProperties = loadDefaultProperties();
//        }
//        return defaultProperties;
//    }
//
//    /**
//     * 加载默认的配置
//     *
//     * @see #DEFAULT_PROPERTIES_RESOURCE_NAME
//     */
//    private Properties loadDefaultProperties() {
//        ClassLoader classLoader = getDefaultClassLoader();
//        List<Properties> defaultPropertiesList = new LinkedList<>();
//        try {
//            Enumeration<URL> defaultPropertiesResources = classLoader.getResources(DEFAULT_PROPERTIES_RESOURCE_NAME);
//            while (defaultPropertiesResources.hasMoreElements()) {
//                URL defaultPropertiesResource = defaultPropertiesResources.nextElement();
//                try (InputStream inputStream = defaultPropertiesResource.openStream();
//                     Reader reader = new InputStreamReader(inputStream, DEFAULT_ENCODING)) {
//                    Properties defaultProperties = new Properties();
//                    defaultProperties.load(reader);
//                    defaultPropertiesList.add(defaultProperties);
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        // sort defaultPropertiesList
//        defaultPropertiesList.sort(DefaultPropertiesComparator.INSTANCE);
//
//        Properties effectiveDefaultProperties = new Properties();
//
//        for (Properties defaultProperties : defaultPropertiesList) {
//            for (String propertyName : defaultProperties.stringPropertyNames()) {
//                if (!effectiveDefaultProperties.containsKey(propertyName)) {
//                    effectiveDefaultProperties.put(propertyName, defaultProperties.getProperty(propertyName));
//                }
//            }
//        }
//
//        return effectiveDefaultProperties;
//    }
//
//    private static class DefaultPropertiesComparator implements Comparator<Properties> {
//
//        public static final Comparator<Properties> INSTANCE = new DefaultPropertiesComparator();
//
//        private DefaultPropertiesComparator() {
//        }
//
//        @Override
//        public int compare(Properties properties1, Properties properties2) {
//            Integer priority1 = getProperty(properties1);
//            Integer priority2 = getProperty(properties2);
//            return Integer.compare(priority1, priority2);
//        }
//
//        private Integer getProperty(Properties properties) {
//            return Integer.decode(properties.getProperty(DEFAULT_PROPERTIES_PRIORITY_PROPERTY_NAME, "0x7fffffff"));
//        }
//    }
//
//    /**
//     * {@inheritDoc}
//     *
//     * @param uri
//     * @param classLoader
//     * @param properties
//     * @return
//     */
//    @Override
//    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
//        // 获取实际的URI
//        URI actualUri = getOrDefault(uri, this::getDefaultURI);
//        // 获取实际的ClassLoader
//        ClassLoader actualClassLoader = getOrDefault(classLoader, this::getDefaultClassLoader);
//        // 获取实际的配置
//        Properties actualProperties = new Properties(getDefaultProperties());
//        if (properties != null && !properties.isEmpty()) {
//            actualProperties.putAll(properties);
//        }
//        String key = generateCacheManagerKey(actualUri, actualClassLoader, actualProperties);
//        // 如果仓库存在直接返回，如果不存在则先创建在放置仓库中并返回
//        return cacheManagerRepository.computeIfAbsent(key, k -> newCacheManager(actualUri, actualClassLoader, actualProperties));
//    }
//
//    @Override
//    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
//        return getCacheManager(uri, classLoader, getDefaultProperties());
//    }
//
//    @Override
//    public CacheManager getCacheManager() {
//        return getCacheManager(getDefaultURI(), getDefaultClassLoader(), getDefaultProperties());
//    }
//
//    @Override
//    public void close() {
//        close(getDefaultClassLoader());
//    }
//
//    @Override
//    public void close(ClassLoader classLoader) {
//        close(getDefaultURI(), classLoader);
//    }
//
//    @Override
//    public void close(URI uri, ClassLoader classLoader) {
//        for (CacheManager cacheManager : cacheManagerRepository.values()) {
//            if (Objects.equals(uri, cacheManager.getURI()) && Objects.equals(classLoader, cacheManager.getClassLoader())) {
//                cacheManager.close();
//            }
//        }
//    }
//
//    @Override
//    public boolean isSupported(OptionalFeature optionalFeature) {
//        return false;
//    }
//
//    private String generateCacheManagerKey(URI uri, ClassLoader classLoader, Properties properties) {
//        StringBuilder keyBuilder = new StringBuilder(uri.toASCIIString())
//                .append("-").append(classLoader)
//                .append("-").append(properties);
//        return keyBuilder.toString();
//    }
//
//    private <T> T getOrDefault(T value, Supplier<T> defaultValue) {
//        return value == null ? defaultValue.get() : value;
//    }
//
//    /**
//     * 获取{@link CacheManager} 的实现类的映射
//     *
//     * @see #CACHE_MANAGER_MAPPING_PROPERTY_PREFIX
//     */
//    private String getCacheManagerClassNamePropertyName(URI uri) {
//        // uri 的 scheme
//        String scheme = uri.getScheme();
//        return CACHE_MANAGER_MAPPING_PROPERTY_PREFIX + scheme;
//    }
//
//    /**
//     * 获取{@link CacheManager} 的实现类的类名
//     */
//    private String getCacheManagerClassName(URI uri, Properties properties) {
//        // 获取 CacheManager 实现类的映射名字
//        String propertyName = getCacheManagerClassNamePropertyName(uri);
//        // 根据映射类名在配置中找到类名
//        String className = properties.getProperty(propertyName);
//        if (className == null) {
//            throw new IllegalStateException(format("The implementation class name of %s that is the value of property '%s' " +
//                    "must be configured in the Properties[%s]", CacheManager.class.getName(), propertyName, properties));
//        }
//        return className;
//    }
//
//    /**
//     * 找出{@link CacheManager} 的实现类，且必须满足是 {@link AbstractCacheManager} 的子类
//     */
//    private Class<? extends AbstractCacheManager> getCacheManagerClass(URI uri, ClassLoader classLoader, Properties properties)
//            throws ClassNotFoundException {
//        // 获取实现类的类名
//        String cacheManagerClassName = getCacheManagerClassName(uri, properties);
//        Class<? extends AbstractCacheManager> cacheManagerImplClass = null;
//        Class<?> cacheManagerClass = classLoader.loadClass(cacheManagerClassName);
//        // The AbstractCacheManager class must be extended by the implementation class,
//        // because the constructor of the implementation class must have four arguments in order:
//        // [0] - CachingProvider
//        // [1] - URI
//        // [2] - ClassLoader
//        // [3] - Properties
//        if (!AbstractCacheManager.class.isAssignableFrom(cacheManagerClass)) {
//            throw new ClassCastException(format("The implementation class of %s must extend %s",
//                    CacheManager.class.getName(), AbstractCacheManager.class.getName()));
//        }
//        cacheManagerImplClass = (Class<? extends AbstractCacheManager>) cacheManagerClass;
//        return cacheManagerImplClass;
//    }
//
//    /**
//     * 创建{@link CacheManager}
//     */
//    private CacheManager newCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
//        CacheManager cacheManager = null;
//        try {
//            // 获取CacheManager 的实现类
//            Class<? extends AbstractCacheManager> cacheManagerClass = getCacheManagerClass(uri, classLoader, properties);
//            // 利用反射进行构造
//            Class[] parameterTypes = new Class[]{CachingProvider.class, URI.class, ClassLoader.class, Properties.class};
//            Constructor<? extends AbstractCacheManager> constructor = cacheManagerClass.getConstructor(parameterTypes);
//            cacheManager = constructor.newInstance(this, uri, classLoader, properties);
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//
//        return cacheManager;
//    }
//
//
//}
