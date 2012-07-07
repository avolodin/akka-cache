package ru.spu.cache.app;

import javax.cache.Caching;

import ru.spu.cache.CacheActorRefApi;
import ru.spu.cache.CacheManager;
import akka.kernel.Bootable;

public class Node3Application implements Bootable {

	private CacheManager cacheManager;
	
	@SuppressWarnings("rawtypes")
	private CacheActorRefApi cache;
	
	@SuppressWarnings("rawtypes")
	private CacheActorRefApi remoteCache;
	
	public Node3Application() {
		cacheManager = (CacheManager) Caching.getCacheManager("node3");
		cache = cacheManager.create("node3Cache", 10 * 1024 * 1024); // cache with 10 MB limit
		remoteCache = cacheManager.getRemote("node1Cache", "127.0.0.1", 2551); // get node1 cache remotely
	}

	@SuppressWarnings("rawtypes")
	public CacheActorRefApi getCache() {
		return cache;
	}

	@SuppressWarnings("rawtypes")
	public CacheActorRefApi getRemoteCache() {
		return remoteCache;
	}

	@Override
	public void shutdown() {
		cacheManager.shutdown();
	}

	@Override
	public void startup() {
		
	}

}
