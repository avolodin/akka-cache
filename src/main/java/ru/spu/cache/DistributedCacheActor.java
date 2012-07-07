package ru.spu.cache;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.spu.cache.msg.ContainsKey;
import ru.spu.cache.msg.Get;
import ru.spu.cache.msg.GetStatistics;
import ru.spu.cache.msg.Put;
import ru.spu.cache.msg.PutIfAbsent;
import ru.spu.cache.msg.Remove;
import ru.spu.cache.msg.RemoveAll;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Duration;
import akka.util.Timeout;

public class DistributedCacheActor<K, V> extends UntypedActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final CacheManager cacheManager;
	
	private DistributedConfig config;
	private List<ActorRef> allRefs;
	private KeyDistribution keyDistribution;

	private Timeout timeout = new Timeout(Duration.parse("1 seconds"));

	public DistributedCacheActor(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public void preStart() {
		super.preStart();
		allRefs = new ArrayList<ActorRef>();
		config = DistributedConfig.load();
		Map<String, InetSocketAddress> remotes = config.getRemotes();
		keyDistribution = new KeyDistribution(remotes);

		for (Map.Entry<String, InetSocketAddress> entry : remotes.entrySet()) {
			allRefs.add(cacheManager.getRemote(entry.getKey(), 
					entry.getValue().getHostName(), entry.getValue().getPort()).getRef());
		}
	}

	@Override
	public void postStop() {
		super.postStop();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ContainsKey<?, ?>) {
			ContainsKey<K, V> msg = (ContainsKey<K, V>) message;
			allRefs.get(keyDistribution.hashForKey(msg.getKey())).forward(msg,
					getContext());
			return;
		}

		if (message instanceof Put<?, ?>) {
			Put<K, V> msg = (Put<K, V>) message;
			allRefs.get(keyDistribution.hashForKey(msg.getKey())).forward(msg,
					getContext());
			return;
		}

		if (message instanceof PutIfAbsent<?, ?>) {
			PutIfAbsent<K, V> msg = (PutIfAbsent<K, V>) message;
			allRefs.get(keyDistribution.hashForKey(msg.getKey())).forward(msg,
					getContext());
			return;
		}

		if (message instanceof Get<?, ?>) {
			Get<K, V> msg = (Get<K, V>) message;
			allRefs.get(keyDistribution.hashForKey(msg.getKey())).forward(msg,
					getContext());
			return;
		}

		if (message instanceof Remove<?, ?>) {
			Remove<K, V> msg = (Remove<K, V>) message;
			allRefs.get(keyDistribution.hashForKey(msg.getKey())).forward(msg,
					getContext());
			return;
		}

		if (message instanceof RemoveAll<?, ?>) {
			RemoveAll<K, V> msg = (RemoveAll<K, V>) message;
			for (ActorRef ref : allRefs) {
				ref.tell(msg);
			}
			return;
		}

		if (message instanceof GetStatistics<?, ?>) {
			CacheStatistics statistics = new CacheStatistics();
			for (ActorRef ref : allRefs) {
				Future<Object> future = Patterns.ask(ref,
						new GetStatistics<K, V>(), timeout);
				CacheStatistics stat = (CacheStatistics) Await.result(future,
						timeout.duration());
				statistics.aggregate(stat);
				getSender().tell(statistics, getSelf());
			}
			return;
		}

		log.info("unknown message received: ", message);
		unhandled(message);
	}

}
