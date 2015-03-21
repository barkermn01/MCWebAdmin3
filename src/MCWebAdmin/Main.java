package MCWebAdmin;

import MCWebAdmin.WebServer.AdminWebServer;

public class Main {

	public static void main(String[] args)
	{
		AdminWebServer.GetInstance().start();
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
			}
		});
	}
}
