package ru.spu.cache;

import java.net.InetSocketAddress;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ru.spu.cache.DistributedConfig;

public class DistributedConfigTest {

	@Test
	public void testConfig() {
		DistributedConfig config = DistributedConfig.load();
		Map<String, InetSocketAddress> remotes = config.getRemotes();
		Assert.assertEquals(3, remotes.size());
		Assert.assertNotNull(remotes.get("node1Cache"));
		Assert.assertEquals("127.0.0.1", remotes.get("node1Cache").getHostName());
		Assert.assertEquals(2551, remotes.get("node1Cache").getPort());
		Assert.assertNotNull(remotes.get("node2Cache"));
		Assert.assertEquals("127.0.0.1", remotes.get("node2Cache").getHostName());
		Assert.assertEquals(2552, remotes.get("node2Cache").getPort());
		Assert.assertNotNull(remotes.get("node3Cache"));
		Assert.assertEquals("127.0.0.1", remotes.get("node3Cache").getHostName());
		Assert.assertEquals(2553, remotes.get("node3Cache").getPort());
	}
}
