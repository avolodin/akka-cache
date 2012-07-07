package ru.spu.cache;

import javax.cache.Cache;
import javax.cache.CacheBuilder;
import javax.cache.Caching;
import javax.cache.OptionalFeature;
import javax.cache.Status;
import javax.transaction.UserTransaction;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

import com.typesafe.config.ConfigFactory;

public class CacheManager implements javax.cache.CacheManager {

	private final String name;

	private final ActorSystem system;

	public CacheManager(String name) {
		this.name = name;
		if (name == null || Caching.DEFAULT_CACHE_MANAGER_NAME.equals(name)) {
			system = ActorSystem.create("CacheApplication");
		} else {
			system = ActorSystem.create("CacheApplication", ConfigFactory
					.load().getConfig(name));
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public ActorSystem getActorSystem() {
		return system;
	}

	/** Creates cache actor. */
	@SuppressWarnings("serial")
	public <V, K> CacheActorRefApi<V, K> create(String cacheName,
			final long limit) {
		ActorRef ref = system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new CacheActor<V, K>(limit);
			}
		}), cacheName);
		return new CacheActorRefApi<V, K>(ref);
	}

	/** Gets local cache actor. */
	public <V, K> CacheActorRefApi<V, K> getLocal(String cacheName) {
		ActorRef ref = system.actorFor("akka://CacheApplication/user/"
				+ cacheName);
		return new CacheActorRefApi<V, K>(ref);
	}

	/** Gets cache actor from remote nodes. */
	public <V, K> CacheActorRefApi<V, K> getRemote(String cacheName,
			String host, int port) {
		ActorRef ref = system.actorFor("akka://CacheApplication@" + host + ":"
				+ port + "/user/" + cacheName);
		return new CacheActorRefApi<V, K>(ref);
	}

	/** Gets distributed cache actor. */
	@SuppressWarnings("serial")
	public <V, K> CacheActorRefApi<V, K> getDistributed(String cacheName) {
		final CacheManager that = this;
		ActorRef ref = system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new DistributedCacheActor<V, K>(that);
			}
		}), cacheName);
		return new CacheActorRefApi<V, K>(ref);
	}

	@Override
	public void shutdown() {
		system.shutdown();
	}

	@Override
	public <K, V> Cache<K, V> getCache(String cacheName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Status getStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <K, V> CacheBuilder<K, V> createCacheBuilder(String cacheName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<Cache<?, ?>> getCaches() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeCache(String cacheName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSupported(OptionalFeature optionalFeature) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UserTransaction getUserTransaction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		throw new UnsupportedOperationException();
	}

}
