package ru.spu.cache.msg;

import java.io.Serializable;

public class Put<K, V> implements Message<K, V>, Serializable {

	private final K key;

	private final V value;

	private final int ttlSecs;

	public Put(K key, V value) {
		this(key, value, 0);
	}

	public Put(K key, V value, int ttlSecs) {
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
