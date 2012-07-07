package ru.spu.cache;

import javax.cache.Caching;

import org.junit.Assert;
import org.junit.Test;

import ru.spu.cache.CacheActorRefApi;
import ru.spu.cache.CacheManager;

import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Duration;
import akka.util.Timeout;

public class LocalCacheTest {

	@Test
	public void testLocal() throws Exception {
		CacheManager cacheManager = (CacheManager) Caching.getCacheManager();
		CacheActorRefApi<Long, String> cache = cacheManager.create("myLocalCache", 10 * 1024 * 1024); // cache with 10 MB limit
		Timeout timeout = new Timeout(Duration.parse("1 seconds"));
		cache.setTimeout(timeout);
		cache.put(1L, "First Value");
		cache.put(2L, "Second Value");
		Future<Object> fValue = cache.get(1l);
		String result = (String) Await.result(fValue, timeout.duration());
		Assert.assertEquals("First Value", result);
		
		CacheActorRefApi<Object, Object> localCache = cacheManager.getLocal("myLocalCache");
		localCache.put(3L, "3 Value");
		fValue = localCache.get(3L);
		result = (String) Await.result(fValue, timeout.duration());
		Assert.assertEquals("3 Value", result);
		
		fValue = localCache.get(1L);
		result = (String) Await.result(fValue, timeout.duration());
		Assert.assertEquals("First Value", result);
		
		cacheManager.shutdown();
	}

}
