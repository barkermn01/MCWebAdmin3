package MCWebAdmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Config.Serializable.Servers;
import MCWebAdmin.WebServer.AdminWebServer;
import MCWebAdmin.WebServer.InstanceWebServer;

public class Main {
	public static boolean shutdown = false;
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{
		/* 
		 * all custom versions of this code base need to change this header
		 * if you charge for your version of this code base remove refund line
		 * do not remove the created by or Version information
		 * you may change version number to add a re-pack version if you wish
		 * do not remove the GitHub URL - only server Admins will see this notice 
		*/
		System.out.println("*************************************************************************");
		System.out.println("***                        MCWebAdmin v3.0.0                          ***");
		System.out.println("*** Created By: Martin Barker                     Modified By: no one ***");
		System.out.println("***     This software is free if you paid for it demand a refund!     ***");
		System.out.println("***           https://github.com/barkermn01/MCWebAdmin3/              ***");
		System.out.println("*************************************************************************");
		
		BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in)); 
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				System.out.println("Shutting Down!");
				if(AdminWebServer.GetInstance().isAlive()){
					// Deprecated should be updated to be a safe kill
					AdminWebServer.GetInstance().stop();
				}
				if(InstanceWebServer.GetInstance().isAlive()){
					// Deprecated should be updated to be a safe kill
					InstanceWebServer.GetInstance().stop();
				}
				shutdown = true;
				try {
					consoleIn.close();
				} catch (IOException e) {
				}
			}
		});

		AdminWebServer.GetInstance().start();
		System.out.println("Starting Admin Web Server on port '"+Global.GetInstance().AdminPort+"'");
		InstanceWebServer.GetInstance().start();
		System.out.println("Starting Instance Web Server on port '"+Global.GetInstance().InstancePort+"'");
		
		while(!shutdown){
			try {
				String cmd = consoleIn.readLine();
				handleCommand(cmd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Deprecated should be updated to be a safe kill
		AdminWebServer.GetInstance().stop();
		// Deprecated should be updated to be a safe kill
		InstanceWebServer.GetInstance().stop();
		System.exit(0);
	}
	
	private static void handleCommand(String cmd){
		if(cmd != null){
			switch(cmd){
				case "exit":
				case "shutdown":
				{
					shutdown = true;
					break;
				}
				case "SaveConfig":
				{
					Global.GetInstance().SaveConfig();
					Servers.GetInstance().SaveConfig();
					Server.SaveAllConfigs();
				}
			}
		}
	}
}
