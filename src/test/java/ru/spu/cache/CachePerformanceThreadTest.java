package ru.spu.cache;

import javax.cache.Caching;

import org.junit.Ignore;
import org.junit.Test;

import ru.spu.cache.CacheActorRefApi;
import ru.spu.cache.CacheManager;

import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Duration;
import akka.util.Timeout;

@Ignore
public class CachePerformanceThreadTest {

	@Test
	public void testLocalCachePerformance() throws Exception {
		int threadCount = 100;
		int operationsPerThread = 100;
		int cacheItemsCount = 1000;
		
		double getProbability = 0.5d;

		CacheManager cacheManager = (CacheManager) Caching.getCacheManager();
		CacheActorRefApi<Integer, String> cache = cacheManager.create("myLocalCache", 10 * 1024 * 1024); // cache with 10 MB limit
		
		// Initialize an array with random values between 0 and cacheSize-1
		// and put these values to cache
		Integer[] testKeys = new Integer[cacheItemsCount];
		for (int j = 0; j < cacheItemsCount; j++) {
			testKeys[j] = new Integer((int) (Math.random() * (cacheItemsCount - 1)));
			cache.put(testKeys[j], String.valueOf(testKeys[j]));
		}
		
		Thread[] threads = new Thread[threadCount];
		// Initialize an array of test threads
		for (int i = 0; i < threadCount; i++) {
			threads[i] = new Thread(new CachePerformanceThread(operationsPerThread, 
					cacheItemsCount, testKeys, cache, getProbability));
		}

		System.out.println("Starting Cache Operations Test Case");
		System.out.println("Totals Thread Count: " + threadCount);
		System.out.println("Number of operations/thread: " + operationsPerThread);

		// Capture the start time for all threads before starting them
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < threadCount; i++) {
			threads[i].start();
		}

		// Join on the array of threads to ensure that all threads have
		// finished before continuing on
		for (int i = 0; i < threadCount; i++) {
			try {
				threads[i].join();
			} catch (java.lang.InterruptedException e) {
			}
		}

		// Output the Total amount of time
		long elapsedTime = System.currentTimeMillis() - startTime;
		long totalOperations = threadCount * operationsPerThread;
		System.out.println("----------------------------------------------");
		System.out.println("Total time: " + elapsedTime + " milliseconds");
		System.out.println("Total operations count: " + totalOperations);
		System.out.println("Operation/second: "
				+ (((long) (totalOperations * 1000)) / elapsedTime) + " operation per second");
	}

	private static class CachePerformanceThread implements Runnable {
		
		private int operationsPerThread;
		
		private int cacheItemsCount;
		
		private Integer[] testKeys;
		
		private CacheActorRefApi<Integer, String> cache;
		
		private double getProbability;
		
		private Timeout timeout = new Timeout(Duration.parse("1 seconds"));

		public CachePerformanceThread(int readsPerThread, int cacheItemsCount,
				Integer[] testKeys, CacheActorRefApi<Integer, String> cache,
				double getProbability) {
			this.operationsPerThread = readsPerThread;
			this.cacheItemsCount = cacheItemsCount;
			this.testKeys = testKeys;
			this.cache = cache;
			this.getProbability = getProbability;
		}

		public void run() {
			boolean isGet = false;
			if (isEventOcurring(getProbability)) {
				isGet = true;
				Future<Object> fValue;
				String cacheValue = null;
				long startTime = System.currentTimeMillis();
				// Perform this.operationsPerThread number of gets.
				for (int i = 0; i < operationsPerThread; i++) {
					fValue = cache.get(testKeys[i % cacheItemsCount]);
					try {
						cacheValue = (String) Await.result(fValue, timeout.duration());
					} catch (Exception e) {
						e.printStackTrace();
					}
					//System.out.println("Cache value: " + cacheValue);
				}
				long endTime = System.currentTimeMillis();
				System.out.println("Get thread time: " + (endTime - startTime)
						+ " milliseconds");
			}
			if (!isGet) {
				long startTime = System.currentTimeMillis();
				// Perform this.operationsPerThread number of puts.
				for (int i = 0; i < operationsPerThread; i++) {
					Integer key = testKeys[i % cacheItemsCount] * 10;
					cache.put(key, String.valueOf(key));
				}
				long endTime = System.currentTimeMillis();
				System.out.println("Put thread time: " + (endTime - startTime)
						+ " milliseconds");
			}
		}
		
		private boolean isEventOcurring(double probability){
			boolean result = false;
			if (probability == 1d){
				result = true;
			} else{
				double randomValue = Math.random();
				if (randomValue <= probability) {
					result = true;
				}
			}
			return result;
		}
		
	}

}
