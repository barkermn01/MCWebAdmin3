package MCWebAdmin.Instance;

import java.util.HashMap;
import java.util.Map;

import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Config.Serializable.Servers;
import MCWebAdmin.Util.Exceptions.ServerNameInUse;

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
	
	public void CreateInstance(String name) throws ServerNameInUse{
		if(Servers.GetInstance().ServerExists(name)){
			throw new ServerNameInUse(name);
		}
	}
	
	public void CreateInstance(Server cfg) throws ServerNameInUse {
		if(Servers.GetInstance().ServerExists(cfg.name)){
			throw new ServerNameInUse(cfg.name);
		}
	}
	
	public void RemoveInstance(String name){
		
	}
	
	public void StopInstance(String name){
		
	}
	
	public void StartInstance(String name){
		
	}
	
	public void RestartInstance(String name){
	
	}
	
	public void SendInstanceNotice(String name, String notice){
		
	}
	
	public void SendWideNotice(){
	
	}
	
	public void BackupInstance(String name, String BackupName){
	
	}
	
	public void RestoreInstance(String name, String BackupName){
		
	}
	
	public void DeleteBackup(String name, String BackupName){
		
	}
}
