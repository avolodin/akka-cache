package ru.spu.cache;

import java.nio.ByteBuffer;

public class CacheEntry {

	private ByteBuffer directByteBuffer;
	private int ttlSecs;
	private long lastAccessedMillis;

	public CacheEntry(ByteBuffer directByteBuffer, int ttlSecs, long lastAccessedMillis) {
		this.directByteBuffer = directByteBuffer;
		this.ttlSecs = ttlSecs;
		this.lastAccessedMillis = lastAccessedMillis;
	}

	public ByteBuffer getDirectByteBuffer() {
		return directByteBuffer;
	}

	public void setDirectByteBuffer(ByteBuffer directByteBuffer) {
		this.directByteBuffer = directByteBuffer;
	}

	public int getTtlSecs() {
		return ttlSecs;
	}

	public void setTtlSecs(int ttlSecs) {
		this.ttlSecs = ttlSecs;
	}

	public long getLastAccessedMillis() {
		return lastAccessedMillis;
	}

	public void setLastAccessedMillis(long lastAccessedMillis) {
		this.lastAccessedMillis = lastAccessedMillis;
	}

}
