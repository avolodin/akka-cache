package ru.spu.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.cache.CacheConfiguration;
import javax.cache.CacheManager;
import javax.cache.Status;
import javax.cache.event.CacheEntryListener;
import javax.cache.mbeans.CacheMXBean;

/** Non thread-safe local cache. */
public class Cache<K, V> implements javax.cache.Cache<K, V> {

	// TODO Self-organizing data structure
	private Map<K, CacheEntry> data = new HashMap<K, CacheEntry>();

	private long limit;
	private long used = 0L;

	static final float WATERMARK = 0.75f;

	CacheStatistics cacheStatistics = new CacheStatistics();

	// ----------------------------------------------------------------------------

	public Cache(long limit) {
		this.limit = limit;
	}

	@Override
	public boolean containsKey(K key) {
		return data.containsKey(key);
	}

	@Override
	public void put(K key, V value) {
		put(key, value, 0);
	}

	public void put(K key, V value, Integer ttlSecs) {
		long t1 = System.currentTimeMillis();

		CacheEntry existEntry = data.get(key);
		if (existEntry != null) {
			ByteBuffer buffer = existEntry.getDirectByteBuffer();
			data.remove(key);
			used -= buffer.capacity();
			DirectByteBufferCleaner.clean(buffer);
		}

		byte[] bytes = serialize(value);
		if (bytes == null) {
			return;
		}
		int size = bytes.length;
		long remaining = limit - used;
		boolean fit = size <= remaining;

		if (!fit) {
			fit = evictUntilUnderWatermakAndFit(size);
		} else {
			ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
			buffer.put(bytes);

			data.put(key, new CacheEntry(buffer, ttlSecs, t1));
			used += size;
			cacheStatistics.cachePuts++;
		}

		long t2 = System.currentTimeMillis();
		cacheStatistics.totalPutMillis += t2 - t1;
	}

	@Override
	public boolean putIfAbsent(K key, V value) {
		return putIfAbsent(key, value, 0);
	}

	public boolean putIfAbsent(K key, V value, Integer ttlSecs) {
		CacheEntry entry = data.get(key);
		if (entry == null) {
			put(key, value, ttlSecs);
			return true;
		}
		entry.setLastAccessedMillis(System.currentTimeMillis());
		return false;
	}

	@Override
	public V get(K key) {
		long t1 = System.currentTimeMillis();
		cacheStatistics.cacheGets++;
		CacheEntry entry = data.get(key);
		if (entry == null) {
			cacheStatistics.cacheMisses++;
			long t2 = System.currentTimeMillis();
			cacheStatistics.totalGetMillis += t2 - t1;
			return null;
		}
		V result = null;
		ByteBuffer buffer = entry.getDirectByteBuffer();
		int dtSecs = (int) ((t1 - entry.getLastAccessedMillis()) / 1000);
		if (entry.getTtlSecs() <= 0 || entry.getTtlSecs() > dtSecs) {
			cacheStatistics.cacheHits++;
			entry.setLastAccessedMillis(t1);
			buffer.rewind();
			byte[] bytes = new byte[buffer.capacity()];
			buffer.get(bytes);
			result = deserialize(bytes);
		} else {
			cacheStatistics.cacheMisses++;
			data.remove(key);
			used -= buffer.capacity();
			DirectByteBufferCleaner.clean(buffer);
		}

		long t2 = System.currentTimeMillis();
		cacheStatistics.totalGetMillis += t2 - t1;
		return result;
	}

	@Override
	public boolean remove(K key) {
		CacheEntry entry = data.remove(key);
		if (entry == null) {
			return false;
		}
		ByteBuffer buffer = entry.getDirectByteBuffer();
		cacheStatistics.cacheRemovals++;
		used -= buffer.capacity();
		DirectByteBufferCleaner.clean(buffer);
		return true;
	}

	@Override
	public void removeAll() {
		for (CacheEntry entry : data.values()) {
			ByteBuffer buffer = entry.getDirectByteBuffer();
			DirectByteBufferCleaner.clean(buffer);
		}
		data.clear();
		cacheStatistics.cacheRemovals++;
		used = 0L;
	}

	@Override
	public CacheStatistics getStatistics() {
		return cacheStatistics;
	}

	// ----------------------------------------------------------------------------

	private boolean evictUntilUnderWatermakAndFit(int size) {
		for (K key : data.keySet()) {
			double ratio = 1.0 * used / limit;
			long remaining = limit - used;
			if (ratio > Cache.WATERMARK || remaining < size) {
				CacheEntry entry = data.remove(key);
				ByteBuffer buffer = entry.getDirectByteBuffer();
				used -= buffer.capacity();
				DirectByteBufferCleaner.clean(buffer);
			} else {
				return true; // break the loop
			}
		}

		long remaining = limit - used;
		return remaining >= size;
	}

	private byte[] serialize(V value) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(value);
			byte[] bytes = baos.toByteArray();
			oos.close();
			baos.close();
			return bytes;
		} catch (IOException e) {
			// TODO handle exception
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private V deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			V value = (V) ois.readObject();
			ois.close();
			bais.close();
			return value;
		} catch (Exception e) {
			// TODO handle exception
			return null;
		}
	}

	// ----------------------------------------------------------------------------

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public Status getStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<K, V> getAll(Set<? extends K> keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Future<V> load(K key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Future<Map<K, ? extends V>> loadAll(Set<? extends K> keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V getAndPut(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(K key, V oldValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V getAndRemove(K key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean replace(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V getAndReplace(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAll(Set<? extends K> keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheConfiguration<K, V> getConfiguration() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean registerCacheEntryListener(
			CacheEntryListener<? super K, ? super V> cacheEntryListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean unregisterCacheEntryListener(
			CacheEntryListener<?, ?> cacheEntryListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object invokeEntryProcessor(K key,
			javax.cache.Cache.EntryProcessor<K, V> entryProcessor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheManager getCacheManager() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<javax.cache.Cache.Entry<K, V>> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheMXBean getMBean() {
		throw new UnsupportedOperationException();
	}

}
