package ru.spu.cache;

import javax.cache.Caching;

import org.junit.Test;

import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Duration;
import akka.util.Timeout;

public class CacheGetPerformanceThreadTest {

	@Test
	public void testLocalCacheGetPerformance() throws Exception {
		int threadCount = 10;
		int readsPerThread = 100;
		int cacheItemsCount = 1000;

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
			threads[i] = new Thread(new CachePerformanceThread(readsPerThread, 
					cacheItemsCount, testKeys, cache));
		}

		System.out.println("Starting ReadOnlyCache Test Case");
		System.out.println("Totals Thread Count: " + threadCount);
		System.out.println("Number of reads/thread: " + readsPerThread);

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
		long totalReads = threadCount * readsPerThread;
		System.out.println("----------------------------------------------");
		System.out.println("Total time: " + elapsedTime + " milliseconds");
		System.out.println("Total reads: " + totalReads + " reads");
		System.out.println("Reads/second: "
				+ (((long) (totalReads * 1000)) / elapsedTime) + " reads per second");
	}

	private static class CachePerformanceThread implements Runnable {
		
		private int readsPerThread;
		
		private int cacheItemsCount;
		
		private Integer[] testKeys;
		
		private CacheActorRefApi<Integer, String> cache;
		
		private Timeout timeout = new Timeout(Duration.parse("5 hours"));

		public CachePerformanceThread(int readsPerThread, int cacheItemsCount,
				Integer[] testKeys, CacheActorRefApi<Integer, String> cache) {
			this.readsPerThread = readsPerThread;
			this.cacheItemsCount = cacheItemsCount;
			this.testKeys = testKeys;
			this.cache = cache;
		}

		public void run() {
			Future<Object> fValue;
			String cacheValue = null;
			long startTime = System.currentTimeMillis();

			// Perform this.readsPerThread number of reads.
			for (int i = 0; i < readsPerThread; i++) {
				fValue = cache.get(testKeys[i % cacheItemsCount]);
				try {
					cacheValue = (String) Await.result(fValue, timeout.duration());
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println("Cache value: " + cacheValue);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("Thread time: " + (endTime - startTime)
					+ " milliseconds");
		}
	}

}
