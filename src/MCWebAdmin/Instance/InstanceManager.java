package MCWebAdmin.Instance;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import MCWebAdmin.Config.Backup;
import MCWebAdmin.Config.Serializable.Backups;
import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Config.Serializable.Servers;
import MCWebAdmin.Util.Exceptions.ServerDoesNotExist;
import MCWebAdmin.Util.Exceptions.ServerIsNotRunning;
import MCWebAdmin.Util.Exceptions.ServerIsRunning;
import MCWebAdmin.Util.Exceptions.ServerNameInUse;

public class InstanceManager {
	private Map<String, Instance> servers;
	private static InstanceManager _inst;
	
	public static InstanceManager GetInstance()
	{
		if(_inst == null){
			_inst = new InstanceManager();
		}
		return _inst;
	}
	
	private InstanceManager()
	{
		servers = new HashMap<>();
		// load all servers
		for(String srvKey : Servers.GetInstance().GetServers().keySet()){
			servers.put(srvKey, new Instance(Server.GetServerInstance(srvKey)));
		}
	}
	
	private void InstallFiles(String name){
		File copyFrom = new File("servers/"+Server.GetServerInstance(name).serverOriginPath+"/");
		File copyTo = new File(Global.GetInstance().InstancesPath+Server.GetServerInstance(name).name+"/");
		if(!copyTo.exists()){
			copyTo.mkdirs();
		}
		try {
			FileUtils.copyDirectoryToDirectory(copyFrom, copyTo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean CheckForInstall(String name){
		File installDir = new File(
			Global.GetInstance().InstancesPath+
			Server.GetServerInstance(name).name+
			"/"+
			Server.GetServerInstance(name).serverOriginPath+
			"/"
			);
		return installDir.exists();
	}
	
	public void UninstallFiles(String name)throws ServerDoesNotExist, ServerIsRunning
	{
		if(!Servers.GetInstance().ServerExists(name)){
			throw new ServerDoesNotExist();
		}
		if(servers.get(name).isRunning()){
			throw new ServerIsRunning(name);
		}
		File installDir = new File(Global.GetInstance().InstancesPath+Server.GetServerInstance(name).name+"/");
		installDir.delete();
	}
	
	public void ReinstallInstance(String name) throws ServerDoesNotExist, ServerIsRunning
	{
		if(!Servers.GetInstance().ServerExists(name)){
			throw new ServerDoesNotExist();
		}
		if(CheckForInstall(name)){
			UninstallFiles(name);
		}
		InstallFiles(name);
	}
	
	public void CreateInstance(String name) throws ServerNameInUse
	{
		if(Servers.GetInstance().ServerExists(name)){
			throw new ServerNameInUse(name);
		}
		Server.GetServerInstance(name);
		Servers.GetInstance().AddServer(name);
	}
	
	public void RemoveInstance(String name) throws ServerDoesNotExist, ServerIsRunning
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		if(servers.get(name).isRunning()){
			throw new ServerIsRunning(name);
		}
		Servers.GetInstance().RemoveServer(name);
	}
	
	public void StopInstance(String name) throws ServerDoesNotExist
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		servers.get(name).Stop();
	}
	
	public void StartInstance(String name) throws ServerDoesNotExist, ServerIsRunning
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		if(servers.get(name).isRunning()){
			throw new ServerIsRunning(name);
		}
		if(!CheckForInstall(name)){
			InstallFiles(name);
		}
		servers.get(name).start();
		
	}
	
	public void RestartInstance(String name) throws ServerDoesNotExist, ServerIsNotRunning
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		if(!servers.get(name).isRunning()){
			throw new ServerIsNotRunning(name);
		}
		servers.get(name).Restart();
	}
	
	public void SendInstanceNotice(String name, String notice) throws ServerDoesNotExist, ServerIsNotRunning
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		if(!servers.get(name).isRunning()){
			throw new ServerIsNotRunning(name);
		}
		servers.get(name).sendInput("say [Instance] "+notice.trim()+"\r\n");
	}
	
	public void SendWideNotice(String notice)
	{
		for(Instance inst : servers.values())
		{
			if(inst.isRunning()){
				inst.sendInput("say [Server] "+notice);
			}
		}
	}
	
	public void BackupInstance(String name, String BackupName) throws ServerDoesNotExist
	{
		Backup bk = new Backup(name, BackupName);
		bk.Create();
	}
	
	public void RestoreInstance(int backupId) throws ServerDoesNotExist
	{
		((HashMap<Integer, Backup>)Backups.getInstance().GetBackups()).get(backupId).Restore();
	}
	
	public void DeleteBackup(int backupId) throws ServerDoesNotExist
	{
		((HashMap<Integer, Backup>)Backups.getInstance().GetBackups()).get(backupId).Delete();
	}
	
	public String[] GetInstancePlayers(String name)
	{
		return servers.get(name).GetPlayers();
	}
}
