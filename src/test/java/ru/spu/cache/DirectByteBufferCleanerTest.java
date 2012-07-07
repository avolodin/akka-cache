package ru.spu.cache;

import java.nio.ByteBuffer;

import org.junit.Test;

import ru.spu.cache.DirectByteBufferCleaner;

public class DirectByteBufferCleanerTest {

	private int LOOP_COUNT = 1000000;
	private int ARRAY_SIZE = 1024;

	@Test
	public void testClean() {
		long t1 = System.currentTimeMillis();
		int i = 0;
		while (i < LOOP_COUNT) {
			clean();
			i++;
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Time: " + (t2 - t1) + " [ms]");
	}

	private void clean() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(ARRAY_SIZE);
		byte[] bytes = new byte[ARRAY_SIZE];
		buffer.put(bytes);
		DirectByteBufferCleaner.clean(buffer);
	}
}
