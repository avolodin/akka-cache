package ru.spu.cache;

import ru.spu.cache.msg.ContainsKey;
import ru.spu.cache.msg.EmptyResult;
import ru.spu.cache.msg.Get;
import ru.spu.cache.msg.GetStatistics;
import ru.spu.cache.msg.Put;
import ru.spu.cache.msg.PutIfAbsent;
import ru.spu.cache.msg.Remove;
import ru.spu.cache.msg.RemoveAll;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CacheActor<K, V> extends UntypedActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	 
	private Cache<K, V> cache;
	
	private long limit;
	
	public CacheActor(long limit) {
		this.limit = limit;
	}

	@Override
	public void preStart() {
		cache = new Cache<K, V>(limit);
		super.preStart();
	}
	
	@Override
	public void postStop() {
		cache.removeAll();
		super.postStop();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ContainsKey<?, ?>) {
			ContainsKey<K, V> msg = (ContainsKey<K, V>) message;
			boolean result = cache.containsKey(msg.getKey());
			getSender().tell(result, getSelf());
			return;
		}
		
		if (message instanceof Put<?, ?>) {
			Put<K, V> msg = (Put<K, V>) message;
			cache.put(msg.getKey(), msg.getValue(), msg.getTtlSecs());
			return;
		}
		
		if (message instanceof PutIfAbsent<?, ?>) {
			PutIfAbsent<K, V> msg = (PutIfAbsent<K, V>) message;
			boolean result = cache.putIfAbsent(msg.getKey(), msg.getValue(), msg.getTtlSecs());
			getSender().tell(result, getSelf());
			return;
		}
		
		if (message instanceof Get<?, ?>) {
			Get<K, V> msg = (Get<K, V>) message;
			V result = cache.get(msg.getKey());
			if (result == null) {
				getSender().tell(new EmptyResult<K, V>(), getSelf());
				return;
			}
			getSender().tell(result, getSelf());
			return;
		}
		
		if (message instanceof Remove<?, ?>) {
			Remove<K, V> msg = (Remove<K, V>) message;
			boolean result = cache.remove(msg.getKey());
			getSender().tell(result, getSelf());
			return;
		}
		
		if (message instanceof RemoveAll<?, ?>) {
			cache.removeAll();
			return;
		}
		
		if (message instanceof GetStatistics<?, ?>) {
			CacheStatistics result = cache.getStatistics();
			getSender().tell(result, getSelf());
			return;
		}
		
		log.info("unknown message received: ", message);
		unhandled(message);
	}

}
