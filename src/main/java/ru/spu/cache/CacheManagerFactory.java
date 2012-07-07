package ru.spu.cache;

import javax.cache.CachingShutdownException;

public class CacheManagerFactory implements javax.cache.CacheManagerFactory {

	@Override
	public CacheManager getCacheManager(String name) {
		CacheManager cacheManager = new CacheManager(name);
		return cacheManager;
	}

	@Override
	public CacheManager getCacheManager(ClassLoader classLoader, String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws CachingShutdownException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean close(ClassLoader classLoader)
			throws CachingShutdownException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean close(ClassLoader classLoader, String name)
			throws CachingShutdownException {
		throw new UnsupportedOperationException();
	}

}
