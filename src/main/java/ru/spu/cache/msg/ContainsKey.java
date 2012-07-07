package ru.spu.cache.msg;

public class ContainsKey<K, V> implements Message<K, V> {

	private final K key;

	public ContainsKey(K key) {
		this.key = key;
	}

	public K getKey() {
		return key;
	}

}
