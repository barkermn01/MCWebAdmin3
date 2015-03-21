package MCWebAdmin.Config.Serializable;

import java.io.Serializable;

public class Global implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Global(){}
	
	// holds Admin Control Panel Port;
	public int AdminPort = 8080; // defaults to '8080'
	// holds Admin Control Panel Hostname
	public String AdminHostname = "*"; // defaults to '*' for all hostnames
	// holds the theme dir name
	public String AdminTheme = "default";
	
	// holds Minecraft Server Control Panel Port
	public int ServerPort = 8081; // defailts to '8081'
	// holds Minecraft Server Control Panel Hostname
	public String ServerHostname = "*"; // defaults to '*' for all hostnames
	// holds the theme dir name
	public String ServerTheme = "default";
	

	private static Global _inst;
	public static Global GetInstance() {
		if(_inst == null){
			_inst = new Global();
		}
		return _inst;
	}
}
