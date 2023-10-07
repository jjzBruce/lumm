package com.lumm.cache.management;

/**
 * {@link CacheStatistics} 假实现
 */
public class DummyCacheStatistics implements CacheStatistics {

    /**
     * Singleton instance
     */
    public static final CacheStatistics INSTANCE = new DummyCacheStatistics();

    private DummyCacheStatistics() {
    }

    @Override
    public CacheStatistics reset() {
        return this;
    }

    @Override
    public CacheStatistics cacheHits() {
        return this;
    }

    @Override
    public CacheStatistics cacheGets() {
        return this;
    }

    @Override
    public CacheStatistics cachePuts() {
        return this;
    }

    @Override
    public CacheStatistics cacheRemovals() {
        return this;
    }

    @Override
    public CacheStatistics cacheEvictions() {
        return this;
    }

    @Override
    public CacheStatistics cacheGetsTime(long costTime) {
        return this;
    }

    @Override
    public CacheStatistics cachePutsTime(long costTime) {
        return this;
    }

    @Override
    public CacheStatistics cacheRemovesTime(long costTime) {
        return this;
    }

    @Override
    public void clear() {

    }

    @Override
    public long getCacheHits() {
        return 0;
    }

    @Override
    public float getCacheHitPercentage() {
        return 0;
    }

    @Override
    public long getCacheMisses() {
        return 0;
    }

    @Override
    public float getCacheMissPercentage() {
        return 0;
    }

    @Override
    public long getCacheGets() {
        return 0;
    }

    @Override
    public long getCachePuts() {
        return 0;
    }

    @Override
    public long getCacheRemovals() {
        return 0;
    }

    @Override
    public long getCacheEvictions() {
        return 0;
    }

    @Override
    public float getAverageGetTime() {
        return 0;
    }

    @Override
    public float getAveragePutTime() {
        return 0;
    }

    @Override
    public float getAverageRemoveTime() {
        return 0;
    }
}
