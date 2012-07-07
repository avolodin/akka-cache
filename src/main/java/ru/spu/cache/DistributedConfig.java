package ru.spu.cache;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;

public class DistributedConfig {

	private Map<String, InetSocketAddress> remotes;

	public Map<String, InetSocketAddress> getRemotes() {
		return remotes;
	}

	public void setRemotes(Map<String, InetSocketAddress> remotes) {
		this.remotes = remotes;
	}

	public static DistributedConfig load() {
		Config config = ConfigFactory.load();
		DistributedConfig distributedConfig = new DistributedConfig();
		Map<String, InetSocketAddress> remotes = new HashMap<String, InetSocketAddress>();
		ConfigList list = config.getList("distributed.remotes");
		Iterator<ConfigValue> it = list.iterator();
		while (it.hasNext()) {
			ConfigValue value = it.next();
			String hostColonPort = value.render();
			String[] remote = hostColonPort.replaceAll("\"", "").split(":");
			InetSocketAddress address = new InetSocketAddress(remote[1],
					Integer.valueOf(remote[2]));
			remotes.put(remote[0], address);
		}
		distributedConfig.setRemotes(remotes);
		return distributedConfig;
	}

}
