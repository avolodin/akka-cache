package ru.spu.cache.app;

import javax.cache.Caching;

import ru.spu.cache.CacheActorRefApi;
import ru.spu.cache.CacheManager;
import akka.kernel.Bootable;

public class DistributedApplication implements Bootable{

private CacheManager cacheManager;
	
	@SuppressWarnings("rawtypes")
	private CacheActorRefApi cache;
	
	public DistributedApplication() {
		cacheManager = (CacheManager) Caching.getCacheManager("distributed");
		cache = cacheManager.getDistributed("distributedCache");
	}

	@SuppressWarnings("rawtypes")
	public CacheActorRefApi getCache() {
		return cache;
	}

	@Override
	public void shutdown() {
		cacheManager.shutdown();
	}

	@Override
	public void startup() {
		
	}
	
}
