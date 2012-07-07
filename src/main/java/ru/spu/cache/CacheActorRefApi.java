package ru.spu.cache;

import ru.spu.cache.msg.ContainsKey;
import ru.spu.cache.msg.Get;
import ru.spu.cache.msg.GetStatistics;
import ru.spu.cache.msg.Put;
import ru.spu.cache.msg.PutIfAbsent;
import ru.spu.cache.msg.Remove;
import ru.spu.cache.msg.RemoveAll;
import akka.actor.ActorRef;
import akka.dispatch.Future;
import akka.pattern.Patterns;
import akka.util.Duration;
import akka.util.Timeout;

public class CacheActorRefApi<K, V> {

	private ActorRef ref;

	private Timeout timeout = new Timeout(Duration.parse("1 seconds"));

	public CacheActorRefApi(ActorRef ref) {
		this.ref = ref;
	}

	public ActorRef getRef() {
		return ref;
	}

	public void setTimeout(Timeout timeout) {
		this.timeout = timeout;
	}

	public Future<Object> containsKey(K key) {
		Future<Object> future = Patterns.ask(ref, new ContainsKey<K, V>(key),
				timeout);
		return future;
	}

	public void put(K key, V value) {
		put(key, value, 0);
	}
	
	public void put(K key, V value, int ttlSecs) {
		ref.tell(new Put<K, V>(key, value, ttlSecs));
	}

	public Future<Object> putIfAbsent(K key, V value) {
		return putIfAbsent(key, value, 0);
	}
	
	public Future<Object> putIfAbsent(K key, V value, int ttlSecs) {
		Future<Object> future = Patterns.ask(ref, new PutIfAbsent<K, V>(key,
				value, ttlSecs), timeout);
		return future;
	}

	public Future<Object> get(K key) {
		Future<Object> future = Patterns.ask(ref, new Get<K, V>(key), timeout);
		return future;
	}

	public Future<Object> remove(K key) {
		Future<Object> future = Patterns.ask(ref, new Remove<K, V>(key),
				timeout);
		return future;
	}

	public void removeAll() {
		ref.tell(new RemoveAll<K, V>());
	}

	public Future<Object> getStatistics(K key) {
		Future<Object> future = Patterns.ask(ref, new GetStatistics<K, V>(),
				timeout);
		return future;
	}
}
