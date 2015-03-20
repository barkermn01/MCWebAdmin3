package MCWebAdmin.Config.Serializable;

import java.io.Serializable;
import java.util.HashMap;


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
	
	private Server(){
		if(Users == null){
			Users = new HashMap<>();
			// if this is a new server add admin support
			Users.put("Admin", "admin");
		}
	}
	
	private static Server _inst;
	public Server GetInstance() {
		if(_inst == null){
			_inst = new Server();
		}
		return _inst;
	}

}
