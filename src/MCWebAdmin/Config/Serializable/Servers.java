package MCWebAdmin.Config.Serializable;

import java.io.Serializable;
import java.util.HashMap;

import MCWebAdmin.Util.Exceptions.ServerDoseNotExist;

public class Servers implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Servers(){		
	}
	
	private static Servers _inst;
	public Servers GetInstance() {
		if(_inst == null){
			_inst = new Servers();
		}
		return _inst;
	}
	
	// holds server name and the path to there config files
	private HashMap<String, String> Servers;
	
	public boolean ServerExists(String name){
		return Servers.containsKey(name);
	}
	
	public String getConfigFilePath(String name) throws ServerDoseNotExist{
		if(Servers.containsKey(name)){
			return Servers.get(name);
		}else{
			throw new ServerDoseNotExist();
		}
	}
	
	
}
