package MCWebAdmin.WebServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class AdminWebWorker extends Thread implements Runnable {
	private Socket soc;
	private BufferedOutputStream bos;
	private BufferedReader br;
	private ServerRequest sr;
	
	public AdminWebWorker(Socket socket) {
		soc = socket;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bos = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void WriteLine(String s)
	{
		byte[] bytes = (s + "\r\n").getBytes();
		try
		{
			bos.write(bytes, 0, bytes.length);
		}
		catch (IOException ex) {}
	}
	
	public void run(){
		
	}
}