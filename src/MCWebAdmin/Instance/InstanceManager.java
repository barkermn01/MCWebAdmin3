package MCWebAdmin.Instance;

import java.util.HashMap;
import java.util.Map;

import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Config.Serializable.Servers;

public class InstanceManager {
	private Map<String, Server> servers;
	private static InstanceManager _inst;
	
	private InstanceManager(){
		servers = new HashMap<>();
		// load all servers
		for(String srvKey : Servers.GetInstance().GetServers().keySet()){
			servers.put(srvKey, Server.GetServerInstance(srvKey));
		}
	}
	
	
}
