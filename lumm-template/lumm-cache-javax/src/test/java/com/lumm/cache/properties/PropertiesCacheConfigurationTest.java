package com.lumm.cache.properties;

import com.lumm.cache.ConfigurableCachingProvider;
import com.lumm.cache.configuration.CacheConfiguration;
import com.lumm.cache.configuration.PropertiesCacheConfiguration;
import com.lumm.cache.event.CacheEntryListenerTest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@link PropertiesCacheConfiguration} Test
 */
public class PropertiesCacheConfigurationTest {

    @Test
    public void test() {
        ConfigurableCachingProvider provider = new ConfigurableCachingProvider();
        PropertiesCacheConfiguration configuration = new PropertiesCacheConfiguration(provider.getDefaultProperties());
        assertEquals(String.class, configuration.getKeyType());
        assertEquals(Integer.class, configuration.getValueType());
        assertTrue(configuration.isStoreByValue());
        assertTrue(configuration.isReadThrough());
        assertTrue(configuration.isWriteThrough());
        assertTrue(configuration.isStatisticsEnabled());
        assertTrue(configuration.isManagementEnabled());
        assertEquals(CacheEntryListenerTest.class, configuration.getCacheEntryListenerConfigurations().iterator().next().getClass());
    }

}
