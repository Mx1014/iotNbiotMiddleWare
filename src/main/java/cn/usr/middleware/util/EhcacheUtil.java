package cn.usr.middleware.util;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.*;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import java.util.concurrent.TimeUnit;


/**
 * @author Administrator
 */
public class EhcacheUtil {


//    public static Cache<String, byte[]> deviceInfoCache;
    public static Cache<String, byte[]> deviceRelationCache;


    static {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10))).build(true);

//        deviceInfoCache = cacheManager.createCache("deviceInfoCache",
//                CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class,
//                        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(5000, EntryUnit.ENTRIES))
//                        .withExpiry(Expirations.timeToLiveExpiration(Duration.of(24 * 60 * 60, TimeUnit.SECONDS)))
//                        .build());
        deviceRelationCache = cacheManager.createCache("deviceRelationCache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(5000, EntryUnit.ENTRIES))
                        .withExpiry(Expirations.timeToLiveExpiration(Duration.of(24 * 60 * 60, TimeUnit.SECONDS)))
                        .build());


    }

}
