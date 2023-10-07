package com.lumm.cache.management;

import javax.cache.management.CacheStatisticsMXBean;

/**
 * 缓存统计接口
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
public interface CacheStatistics extends CacheStatisticsMXBean {

    CacheStatistics reset();

    CacheStatistics cacheHits();

    CacheStatistics cacheGets();

    CacheStatistics cachePuts();

    CacheStatistics cacheRemovals();

    CacheStatistics cacheEvictions();

    CacheStatistics cacheGetsTime(long costTime);

    CacheStatistics cachePutsTime(long costTime);

    CacheStatistics cacheRemovesTime(long costTime);

}
