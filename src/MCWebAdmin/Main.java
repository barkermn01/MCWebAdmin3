package MCWebAdmin;

import MCWebAdmin.WebServer.AdminWebServer;
import MCWebAdmin.WebServer.InstanceWebServer;

public class Main {

	public static void main(String[] args)
	{
		AdminWebServer.GetInstance().start();
		System.out.println("Starting Admin Web Server");
		InstanceWebServer.GetInstance().start();
		System.out.println("Starting Instance Web Server");
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				if(AdminWebServer.GetInstance().isAlive()){
					try {
						AdminWebServer.GetInstance().join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(InstanceWebServer.GetInstance().isAlive()){
					try {
						InstanceWebServer.GetInstance().join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
