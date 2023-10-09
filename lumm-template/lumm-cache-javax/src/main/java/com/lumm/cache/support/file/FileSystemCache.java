package com.lumm.cache.support.file;


import com.lumm.cache.AbstractCache;
import com.lumm.cache.ExpirableEntry;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.File;
import java.util.Set;

/**
 * 缓存实现，基于文件系统，线程不安全
 * todo 未完全实现
 */
public class FileSystemCache<K, V> extends AbstractCache<K, V> {

    private final File cacheDirectory;

    protected FileSystemCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        super(cacheManager, cacheName, configuration);
        cacheDirectory = new File(cacheManager.getURI().getPath());
    }

    private File entryFile(K key) {
        return new File(cacheDirectory, String.valueOf(key));
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        File entryFile = entryFile(key);
        return entryFile.exists();
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        return null;
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException {

    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        return null;
    }

    @Override
    protected void clearEntries() throws CacheException {

    }

    @Override
    protected Set<K> keySet() {
        return null;
    }
}
