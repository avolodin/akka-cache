package ru.spu.cache.msg;

import java.io.Serializable;

public class Get<K, V> implements Message<K, V>, Serializable {

	private final K key;

	public Get(K key) {
		this.key = key;
	}

	public K getKey() {
		return key;
	}

}
