package com.lumm.cache.integration;

import lombok.extern.slf4j.Slf4j;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.*;

import static java.lang.String.format;

/**
 * 缓存加载写入实现，基于文件
 */
@Slf4j
public class FileFallbackStorage extends AbstractFallbackStorage<Object, Object> {

    /**
     * 文件路径
     */
    private static final File CACHE_FALLBACK_DIRECTORY = new File(".cache/fallback/");

    /**
     * 构造
     */
    public FileFallbackStorage() {
        // 最小优先级，兜底
        super(Integer.MAX_VALUE);
        // 创建文件路径
        makeCacheFallbackDirectory();
    }

    /**
     * 根据缓存健获取对应的文件
     *
     * @param key 缓存健
     * @return File
     */
    File toStorageFile(Object key) {
        return new File(CACHE_FALLBACK_DIRECTORY, key.toString() + ".dat");
    }

    /**
     * 加载数据
     *
     * @param key the key identifying the object being loaded
     * @return Object
     * @throws CacheLoaderException
     */
    @Override
    public Object load(Object key) throws CacheLoaderException {
        File storageFile = toStorageFile(key);
        if (!storageFile.exists() || !storageFile.canRead()) {
            log.warn(format("存储文件[路径:%s]无法读取，值无法被加载。", storageFile.getAbsolutePath()));
            return null;
        }
        Object value = null;
        try (FileInputStream inputStream = new FileInputStream(storageFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            value = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error(format("序列化值[%s]失败，原因：%s", value, e.getMessage()));
        }
        return value;
    }

    /**
     * 写入数据
     * @param entry 缓存键值对象
     * @throws CacheWriterException
     */
    @Override
    public void write(Cache.Entry<?, ?> entry) throws CacheWriterException {
        Object key = entry.getKey();
        Object value = entry.getValue();
        File storageFile = toStorageFile(key);
        if (storageFile.exists() && !storageFile.canWrite()) {
            log.warn(format("存储文件[路径:%s]无法写入，值无法被存储。", storageFile.getAbsolutePath()));
            return;
        }
        try (FileOutputStream outputStream = new FileOutputStream(storageFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        ) {
            objectOutputStream.writeObject(value);
        } catch (IOException e) {
            log.error(format("序列化值[%s]失败，原因：%s", value, e.getMessage()));
        }
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        File storageFile = toStorageFile(key);
        storageFile.delete();
    }

    @Override
    public void destroy() {
        destroyCacheFallbackDirectory();
    }

    private void destroyCacheFallbackDirectory() {
        if (CACHE_FALLBACK_DIRECTORY.exists()) {
            // Delete all files into directory
            for (File storageFile : CACHE_FALLBACK_DIRECTORY.listFiles()) {
                storageFile.delete();
            }
        }
    }

    /**
     * 创建缓存文件路径
     */
    private void makeCacheFallbackDirectory() {
        if (!CACHE_FALLBACK_DIRECTORY.exists() && !CACHE_FALLBACK_DIRECTORY.mkdirs()) {
            throw new IllegalStateException(String.format("缓存加载写入文件路径[%s]不能被创建!", CACHE_FALLBACK_DIRECTORY));
        }
    }
}
