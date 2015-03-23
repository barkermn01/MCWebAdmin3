package MCWebAdmin.Config.Serializable;

import java.io.Serializable;
import java.util.HashMap;

import MCWebAdmin.Config.ConfigReader;


public class Server implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// holds users and passwords
	public HashMap<String, String> Users;
	
	// holds the servers name
	public String name = "Default"; // Defaults to 'Default'
	
	// holds the path to the server jar to run
	public String path = "Tekkit.jar"; // Defaults to 'Tekkit.jar'
	
	// holds the directory to run the server from for unique users
	public String baseDir = "./"; // Defaults to './'
	
	// holds the -Xms value for this server
	public String MemoryMin = "256M"; // Defaults to '256M'
	
	// holds the -Xmx value for this server
	public String MemoryMax = "512M"; // Defaults to '512M'
	
	// holds the path to the java path so some servers can use older versions of java
	public String JavaPath = "java"; // Defaults to Path java based java
	
	// holds the name of the theme to be loaded for this servers control panel
	public String ThemeName = "terminal"; // defaults to 'terminal'
	
	private static HashMap<String,Server> _inst = new HashMap<>();
	public static Server GetServerInstance(String name)
	{
		Server eval = null;
		boolean cfgExists = ConfigReader.GetInstance().ConfigExists("Servers/"+name+".cfg");
		if(!_inst.containsKey(name) && !cfgExists){
			_inst.put(name, new Server());
		}else if(cfgExists){
			_inst.put(name, ConfigReader.GetInstance().Read(eval, "Server/"+name+".cgf"));
		}
		return _inst.get(name);
	}
	
	public static void SaveAllConfigs()
	{
		for(Server srv : _inst.values())
		{
			srv.SaveConfig();
		}
	}
	
	public void SaveConfig()
	{
		ConfigReader.GetInstance().Write(this, "Server/"+name+".cgf");
	}
	
	private Server(){
		if(Users == null){
			Users = new HashMap<>();
			// if this is a new server add admin support
			Users.put("Admin", "admin");
		}
	}
}
