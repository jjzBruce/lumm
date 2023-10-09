package com.lumm.cache;


import com.lumm.cache.support.memory.InMemoryCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * 键值类型对
 */
@Slf4j
public class KeyValueTypePairTest {

    @Test
    public void testResolve() {
        KeyValueTypePair keyValueTypePair = KeyValueTypePair.resolve(InMemoryCache.class);
        Assert.assertEquals(Object.class, keyValueTypePair.getKeyType());
        Assert.assertEquals(Object.class, keyValueTypePair.getValueType());
    }

}
