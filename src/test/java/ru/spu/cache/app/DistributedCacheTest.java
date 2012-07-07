package ru.spu.cache.app;

import org.junit.Assert;
import org.junit.Test;

import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Duration;
import akka.util.Timeout;

import ru.spu.cache.CacheActorRefApi;
import ru.spu.cache.msg.EmptyResult;

public class DistributedCacheTest {

	@Test
	public void testDistributed() throws Exception {
		Node1CacheThread node1App = new Node1CacheThread();
		Thread node1Thread = new Thread(node1App);
		node1Thread.start();
		
		Node2CacheThread node2App = new Node2CacheThread();
		Thread node2Thread = new Thread(node2App);
		node2Thread.start();
		
		Node3CacheThread node3App = new Node3CacheThread();
		Thread node3Thread = new Thread(node3App);
		node3Thread.start();
		
		DistributedCacheThread distributedApp = new DistributedCacheThread();
		Thread distributedThread = new Thread(distributedApp);
		distributedThread.start();
		
		node1Thread.join();
		node2Thread.join();
		node3Thread.join();
		distributedThread.join();
		
		if (!node1App.isSuccess()) {
			Assert.fail();
		}
		if (!node3App.isSuccess()) {
			Assert.fail();
		}
		if (!node2App.isSuccess()) {
			Assert.fail();
		}
		if (!distributedApp.isSuccess()) {
			Assert.fail();
		}
	}
	
	private static class Node1CacheThread implements Runnable {

		private boolean success = true;
		
		@SuppressWarnings("unused")
		@Override
		public void run() {
			try {
				Node1Application app = new Node1Application();
				System.out.println("Started Node1 Cache Application");
			} catch (Throwable e) {
				e.printStackTrace();
				success = false;
			}
		}
		
		public boolean isSuccess() {
			return success;
		}
		
	}
	
	private static class Node2CacheThread implements Runnable {

		private boolean success = true;
		
		@SuppressWarnings("unused")
		@Override
		public void run() {
			try {
				Node2Application app = new Node2Application();
				System.out.println("Started Node 2 Cache Application");
			} catch (Exception e) {
				e.printStackTrace();
				success = false;
			}
		}
		
		public boolean isSuccess() {
			return success;
		}
	}
	
	private static class Node3CacheThread implements Runnable {

		private boolean success = true;
		
		@SuppressWarnings("unused")
		@Override
		public void run() {
			try {
				Node3Application app = new Node3Application();
				System.out.println("Started Node 3 Cache Application");
			} catch (Throwable e) {
				e.printStackTrace();
				success = false;
			}
 		}
		
		public boolean isSuccess() {
			return success;
		}
		
	}
	
	private static class DistributedCacheThread implements Runnable {

		private boolean success = true;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void run() {
			try {
				DistributedApplication app = new DistributedApplication();
				System.out.println("Started Distributed Cache Application");
				CacheActorRefApi cache = app.getCache();
				Timeout timeout = new Timeout(Duration.parse("1 seconds"));
				cache.setTimeout(timeout);
				cache.put(1L, "First Value");
				cache.put(2L, "Second Value");
				cache.put(3L, "Third Value");
				Future<Object> fValue = cache.get(1L);
				String result = (String) Await.result(fValue, timeout.duration());
				Assert.assertEquals("First Value", result);
				fValue = cache.get(5L);
				Object res = (Object) Await.result(fValue, timeout.duration());
				if (!(res instanceof EmptyResult)) {
					success = false;
				}
			} catch (Throwable e) {
				e.printStackTrace();
				success = false;
			}
 		}
		
		public boolean isSuccess() {
			return success;
		}
		
	}
	
}
