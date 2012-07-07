package ru.spu.cache;

import java.nio.ByteBuffer;
import java.lang.Class;
import java.lang.reflect.Method;

/**
 * Frees memory of direct buffer allocated by ByteBuffer.allocateDirect without
 * waiting for GC.
 * 
 * See:
 * http://groups.google.com/group/netty/browse_thread/thread/3be7f573384af977
 * https://github.com/netty/netty/issues/62
 */
public class DirectByteBufferCleaner {

	private static Method cleanerMethod() {
		try {
			Class<?> clazz = Class.forName("java.nio.DirectByteBuffer");
			Method method = clazz.getDeclaredMethod("cleaner", null);
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
			return null;
		}
	}

	private static Method cleanMethod() {
		try {
			Class<?> clazz = Class.forName("sun.misc.Cleaner");
			Method method = clazz.getDeclaredMethod("clean", null);
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Does nothing if java.nio.DirectByteBuffer or sun.misc.Cleaner is not
	 * found
	 */
	public static void clean(ByteBuffer buffer) {
		try {
			Method cleanerMethod = cleanerMethod();
			Method cleanMethod = cleanMethod();
			if (cleanerMethod != null && cleanMethod != null) {
				Object cleaner = cleanerMethod.invoke(buffer);
				cleanMethod.invoke(cleaner);
			}
		} catch (Exception e) {
		}
	}

}
