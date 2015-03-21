package MCWebAdmin.WebServer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class AdminWebServer extends WebServer {

	ArrayList<AdminWebWorker> workers;
	
	@Override
	public void run() {
		while(!this.shutdown){
			// TODO Auto-generated method stub
			try {
				Socket socket = server.accept();
				AdminWebWorker worker = new AdminWebWorker(socket);
				workers.add(worker);
				worker.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(AdminWebWorker worker : workers)
		{
			if(worker.isAlive()){
				try {
					worker.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
