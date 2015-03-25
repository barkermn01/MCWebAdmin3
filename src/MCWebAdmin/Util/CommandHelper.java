package MCWebAdmin.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import MCWebAdmin.Config.Serializable.Backups;
import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Config.Serializable.Servers;
import MCWebAdmin.Instance.InstanceManager;
import MCWebAdmin.Util.Exceptions.ServerDoesNotExist;
import MCWebAdmin.Util.Exceptions.ServerIsRunning;
import MCWebAdmin.Util.Exceptions.ServerNameInUse;
import MCWebAdmin.WebServer.AdminWebServer;

public class CommandHelper {
	private BufferedReader consoleIn;
	public CommandHelper(BufferedReader in)
	{
		consoleIn = in;
	}
	
	public boolean exit(){
		try {
			boolean test = false;
			System.out.print("Are you sure you want to exit settings are not saved automaticly [Y/N]:");
			String sure = consoleIn.readLine();
			if(sure.toLowerCase().equals("y") || sure.toLowerCase().equals("n"))
			{
				test = true;
			}
			while(!test){
				sure = consoleIn.readLine();
				System.out.print("Response can only be [Y/N]:");
				if(sure.toLowerCase().equals("y") || sure.toLowerCase().equals("n"))
				{
					test = true;
				}
			}
			return sure.toLowerCase().equals("y");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void help()
	{
		System.out.println("Help text - this is the list of commands you can use!  ");
		System.out.println("Shutdown|Exit		- Stops this server running");
		System.out.println("SaveConfig|save		- Forces a save of all config files");
		System.out.println("AdminPort		- Change your admin web access port");
		System.out.println("InstancePort		- Change the instance web access port");
		System.out.println("Create			- Create a new instance of minecraft server");
		System.out.println("Start			- Start a created instance");
		System.out.println("Stop			- Stop a started instance");
		System.out.println("Restart			- Restart a started instance");
		System.out.println("Delete			- Delete a created instance");
		System.out.println("Backup			- Create a backup of an instance");
		System.out.println("ListBackups		- List all the backups saved");
		System.out.println("ListInstances		- Lists all the instances current created");
		System.out.println("Restore			- Restores a instance from a backup");
		System.out.println("Help			- Shows this help information");
	}
	
	public void restore()
	{
		System.out.print("Please enter backup filename: ");
		try {
			String bkName = consoleIn.readLine();
			InstanceManager.GetInstance().RestoreInstance(bkName);
		} catch (NumberFormatException e) {
			System.out.println("Port number is invalid");
		} catch (ServerDoesNotExist e) {
			System.out.println("This backup was made for a server that no longer exists");
		} catch (IOException e) {
		}		
	}
	
	public void listInstances()
	{

		System.out.println("List of current instances:");
		Set<String> servers = Servers.GetInstance().GetServers().keySet();
		for(String s : servers)
		{
			System.out.println(s);
		}
		System.out.println("List completed. Total of "+servers.size()+" servers");
	}
	
	public void listBackups()
	{

		System.out.println("List of current backups:");
		System.out.println("Backups are named as ServerName_BackupName_DateOfBackup");
		ArrayList<String> data = Backups.getInstance().GetBackups();
		for(String bk : data){
			System.out.println(bk);
		}
		System.out.println("List completed. Total of "+data.size()+" backups");
	}
	
	public void backup()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			System.out.print("Please enter backup name: ");
			String bk = consoleIn.readLine();
			InstanceManager.GetInstance().BackupInstance(name, bk);
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		}
	}
	
	public void delete()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			InstanceManager.GetInstance().RemoveInstance(name);
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		} catch (ServerIsRunning e) {
			System.out.println("Instance is currently running");
		}
	}
	
	public void restart()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			InstanceManager.GetInstance().RestartInstance(name);;
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		}
	}
	
	public void stop()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			InstanceManager.GetInstance().StopInstance(name);;
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		} 
	}
	
	public void start()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			System.out.println("Starting instance: "+name);
			InstanceManager.GetInstance().StartInstance(name);
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		} catch (ServerIsRunning e) {
			System.out.println("Instance is already running");
		}
	}
	
	public void create()
	{
		System.out.print("Please enter new instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			InstanceManager.GetInstance().CreateInstance(name);
		} catch (IOException e) {
		} catch (ServerNameInUse e) {
			System.out.println("Sorry instance name is in use");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void instancePort()
	{
		System.out.print("Please enter new port: ");
		try {
			int port = Integer.parseInt(consoleIn.readLine());
			Global.GetInstance().AdminPort = port;
			Global.GetInstance().SaveConfig();
			AdminWebServer.GetInstance().stop();
			AdminWebServer.GetInstance().start();
		} catch (NumberFormatException e) {
			System.out.println("Port number is invalid");
		} catch (IOException e) {
		}
	}
	
	@SuppressWarnings("deprecation")
	public void adminPort()
	{
		System.out.print("Please enter new port: ");
		try {
			int port = Integer.parseInt(consoleIn.readLine());
			Global.GetInstance().AdminPort = port;
			Global.GetInstance().SaveConfig();
			AdminWebServer.GetInstance().stop();
			AdminWebServer.GetInstance().start();
		} catch (NumberFormatException e) {
			System.out.println("Port number is invalid");
		} catch (IOException e) {
		}
	}
	
	public void saveConfig()
	{

		Global.GetInstance().SaveConfig();
		Servers.GetInstance().SaveConfig();
		Server.SaveAllConfigs();
		Backups.getInstance().SaveConfig();
	}
}
