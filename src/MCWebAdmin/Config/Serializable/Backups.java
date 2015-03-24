package MCWebAdmin.Config.Serializable;

import java.io.Serializable;
import java.util.HashMap;

import MCWebAdmin.Config.Backup;
import MCWebAdmin.Config.ConfigReader;

public class Backups implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, Backup> backups;
	private int backupIndex = 0;
	private static Backups _inst;
	
	private Backups()
	{
		backups = new HashMap<>();
	}
	
	public static Backups getInstance()
	{
		boolean cfgExists = ConfigReader.GetInstance().ConfigExists("Backups.cfg");
		if(_inst == null && !cfgExists){
			_inst = new Backups();
		}else if(cfgExists){
			_inst = ConfigReader.GetInstance().Read(_inst, "Backups.cgf");
		}
		return _inst;
	}
	
	public void SaveConfig(){
		ConfigReader.GetInstance().Write(this, "Backups.cfg");
	}
	
	public void AddBackup(Backup bk)
	{
		backups.put(backupIndex, bk);
		backupIndex++;
		SaveConfig();
	}
	
	public void RemoveBackup(Backup bk)
	{
		backups.remove(bk);
		SaveConfig();
	}
	
	public HashMap<Integer, Backup> GetBackups()
	{
		return backups;
	}
}
