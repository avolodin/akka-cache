package ru.spu.cache.msg;

public class PutIfAbsent<K, V> implements Message<K, V> {

	private final K key;

	private final V value;

	private final int ttlSecs;

	public PutIfAbsent(K key, V value) {
		this(key, value, 0);
	}

	public PutIfAbsent(K key, V value, int ttlSecs) {
		this.key = key;
		this.value = value;
		this.ttlSecs = ttlSecs;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public int getTtlSecs() {
		return ttlSecs;
	}

}
