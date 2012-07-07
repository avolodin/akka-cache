package ru.spu.cache.app;

import javax.cache.Caching;

import ru.spu.cache.CacheActorRefApi;
import ru.spu.cache.CacheManager;
import akka.kernel.Bootable;

public class Node1Application implements Bootable {

	private CacheManager cacheManager;
	
	@SuppressWarnings("rawtypes")
	private CacheActorRefApi cache;
	
	public Node1Application() {
		cacheManager = (CacheManager) Caching.getCacheManager("node1");
		cache = cacheManager.create("node1Cache", 10 * 1024 * 1024); // cache with 10 MB limit
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
