package ru.spu.cache;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ru.spu.cache.KeyDistribution;

public class KeyDistributionTest {

	@Test
	public void testDistribution() throws Exception {
		Map<String, InetSocketAddress> addresses = new HashMap<String, InetSocketAddress>();
		addresses.put("node1Cache", new InetSocketAddress("localhost", 2552));
		addresses.put("node2Cache", new InetSocketAddress("localhost", 2553));
		addresses.put("node3Cache", new InetSocketAddress("localhost", 2554));

		KeyDistribution keyDistribution = new KeyDistribution(addresses);
		Assert.assertEquals(1, keyDistribution.hashForKey("555"));
		Assert.assertEquals(1, keyDistribution.hashForKey("555"));
	}
}
