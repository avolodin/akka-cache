Akka based cache project
========================

This is an educational version of java cache library based on Akka.
The cache can be local, remote or distributed.
It implements some features of JSR107 Cache Specification

Local cache
-----------

Create a cache with 10 MB limit:
  
  CacheManager cacheManager = (CacheManager) Caching.getCacheManager();
  CacheActorRefApi<Long, String> cache = cacheManager.create("myLocalCache", 10 * 1024 * 1024);

Put:

  cache.put(1L, "First Value");

Get:
  
  Future<Object> fValue = cache.get(1l);
  Timeout timeout = new Timeout(Duration.parse("1 seconds"));
  String result = (String) Await.result(fValue, timeout.duration());

Remove:

  cache.remove(1L)
  cache.removeAll()

Conditional put:

  cache.putIfAbsent(2L, "Second Value")

Time to live:

  cache.put(1L, "First Value", 5)            // Cache entry will be invalidated after 5 seconds
  cache.putIfAbsent(2L, "Second Value", 10)  // 10 seconds


Remote cache
------------

@See src/main/resources/applicaton.conf
@See Akka remoting http://doc.akka.io/docs/akka/2.0.1/java/remoting.html

On node 1:
  
  cache = cacheManager.create("node1Cache", 10 * 1024 * 1024);

On node 2:
  cache = cacheManager.create("node2Cache", 10 * 1024 * 1024);

On node 3:
  cache = cacheManager.create("node3Cache", 10 * 1024 * 1024);
  remoteCache = cacheManager.getRemote("node1Cache", "127.0.0.1", 2551);
        
On node 1:
    cache.put(1L, "First Value");
    
On node 3:

    Future<Object> fValue = remoteCache.get(1L);
    String result = (String) Await.result(fValue, timeout.duration());


Distributed cache
-----------------

@See src/main/resources/applicaton.conf

Cache will be distributed among multiple nodes.
It is used a simple algorthm
h(key) = crc32(key) % server count

After running caches on node1, node2 and node 3 on client node you can use:

    cache = cacheManager.getDistributed("distributedCache");
    cache.put(1L, "First Value");
    cache.put(2L, "Second Value");
    Timeout timeout = new Timeout(Duration.parse("1 seconds"));
    Future<Object> fValue = cache.get(1L);
    String result = (String) Await.result(fValue, timeout.duration());


FYI
---

* The idea of this java library was inspired by the cache library for scala https://github.com/ngocdaothanh/CleanerAkka
* Akka: http://akka.io/
* JSR107 Cache Specification: https://github.com/jsr107/jsr107spec
