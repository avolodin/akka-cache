package ru.spu.cache.msg;

public class Remove<K, V> implements Message<K, V> {

	private final K key;

	public Remove(K key) {
		this.key = key;
	}

	public K getKey() {
		return key;
	}
	
}
