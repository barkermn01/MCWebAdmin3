package MCWebAdmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import MCWebAdmin.Config.Backup;
import MCWebAdmin.Config.Serializable.Backups;
import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Config.Serializable.Servers;
import MCWebAdmin.Instance.InstanceManager;
import MCWebAdmin.Util.CommandHelper;
import MCWebAdmin.Util.Exceptions.ServerDoesNotExist;
import MCWebAdmin.Util.Exceptions.ServerIsNotRunning;
import MCWebAdmin.Util.Exceptions.ServerIsRunning;
import MCWebAdmin.Util.Exceptions.ServerNameInUse;
import MCWebAdmin.WebServer.AdminWebServer;
import MCWebAdmin.WebServer.InstanceWebServer;

public class Main {
	public static boolean shutdown = false;
	private static BufferedReader consoleIn;
	private static CommandHelper commandHelper;
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{
		consoleIn = new BufferedReader(new InputStreamReader(System.in)); 
		commandHelper = new CommandHelper(consoleIn);
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
				
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				InstanceManager.GetInstance().StopAllInstances();
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
				System.out.print(">");
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

		InstanceManager.GetInstance().ForceStopAllInstances();
		System.exit(0);
	}
	
	private static void handleCommand(String cmd){
		if(cmd != null){
			switch(cmd.toLowerCase()){
				case "exit":
				case "shutdown":
				{
					shutdown = commandHelper.exit();
					
					break;
				}
				case "save":
				case "saveconfig":
				{
					commandHelper.saveConfig();
					break;
				}
				case "adminport":
				{
					commandHelper.adminPort();
					break;
				}
				case "instanceport":
				{
					commandHelper.instancePort();
					break;
				}
				case "create":
				{
					commandHelper.create();
					break;
				}
				case "start":
				{
					commandHelper.start();
					break;
				}
				case "stop":
				{
					commandHelper.stop();
					break;
				}
				case "restart":
				{
					commandHelper.restart();
					break;
				}
				case "delete":
				{
					commandHelper.delete();
					break;
				}
				case "backup":
				{
					commandHelper.backup();
					break;
				}
				case "listbackups":
				{
					commandHelper.listBackups();
					break;
				}
				case "listinstances":
				{
					commandHelper.listInstances();
					break;
				}
				case "restore":
				{
					commandHelper.restore();
					break;
				}
				case "help":
				{
					commandHelper.help();
					break;
				}
			}
		}
	}
}
