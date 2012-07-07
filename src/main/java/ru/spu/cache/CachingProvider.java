package ru.spu.cache;

import javax.cache.OptionalFeature;

public class CachingProvider implements javax.cache.spi.CachingProvider {

	public CachingProvider() {
		
	}

	@Override
	public CacheManagerFactory getCacheManagerFactory() {
		return new CacheManagerFactory();
	}

	@Override
	public boolean isSupported(OptionalFeature optionalFeature) {
		return true;
	}

}
