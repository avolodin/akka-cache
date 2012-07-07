package ru.spu.cache;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.zip.CRC32;

public class KeyDistribution {

	private Map<String, InetSocketAddress> addresses;

	public KeyDistribution(Map<String, InetSocketAddress> addresses) {
		this.addresses = addresses;
	}

	public int hashForKey(Object key) {
		if (addresses == null || addresses.isEmpty()) {
			return 0;
		}
		CRC32 crc = new CRC32();
		crc.update(key.toString().getBytes());
		long value = crc.getValue();
		int result = (int) (value % addresses.size());
		return result;
	}

	public Map<String, InetSocketAddress> getAddresses() {
		return addresses;
	}

	public void setAddresses(Map<String, InetSocketAddress> addresses) {
		this.addresses = addresses;
	}

}
