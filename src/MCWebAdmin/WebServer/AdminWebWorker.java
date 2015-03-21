package MCWebAdmin.WebServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Util.FileReader;
import MCWebAdmin.Util.SupportedMimeTypes;
import MCWebAdmin.Util.Exceptions.FileNotFound;

public class AdminWebWorker extends Thread implements Runnable {
	private Socket soc;
	private BufferedOutputStream bos;
	private BufferedReader br;
	private ServerRequest sr;
	private String basePath = "Admin_Web/"+Global.GetInstance().ServerTheme + "/";
	
	public AdminWebWorker(Socket socket) {
		soc = socket;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bos = new BufferedOutputStream(socket.getOutputStream());
			sr = new ServerRequest();
			sr.parseInput(br);
			
			// are we handling a JSON RPC Request
			if("/rpc/".equals(sr.requestPath) || "rpc".equals(sr.requestPage) && "/".equals(sr.requestPath)){
				WriteLine("HTTP/1.0 200");
				WriteLine("Server: MCWebAdmin/3.0.0");
				WriteLine("Content-Type: text/plain");
				WriteLine("");
				WriteLine(sr.rawPost);
				bos.flush();
				bos.close();
				bos.close();
				return;
			}else{
				String path = basePath+sr.requestPath+sr.requestPage;
				String fileExt = sr.requestPage.substring(0, sr.requestPage.lastIndexOf('.'));
				String mimeType = SupportedMimeTypes.GetInstance().getMimeType(fileExt);
				try{
					byte[] read = FileReader.GetInstance().readFile(path);
					WriteLine("HTTP/1.0 200");
					WriteLine("Content-Type: " + mimeType);
					WriteLine("");
					bos.write(read, 0, read.length);
					bos.flush();
					bos.close();
					bos.close();
					return;
				}
				catch(FileNotFound fnf){
					WriteLine("HTTP/1.0 404");
					WriteLine("Content-Type: text/html");
					WriteLine("");
					WriteLine("<!DOCTYPE html><html><body><h1>404 File Not Found</h1></body></html>");
					bos.flush();
					bos.close();
					bos.close();
					return;
				}
				catch(Exception e){
					show500Error();
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private MCWebAdmin.Util.FileReader GetInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	private void show500Error(){
		WriteLine("HTTP/1.0 500");
		WriteLine("Content-Type: text/html");
		WriteLine("");
		WriteLine("<!DOCTYPE html><html><body><h1>500 Internal Server Error</h1></body></html>");
		try {
			bos.flush();
			bos.close();
			bos.close();
		} catch (Exception e) {
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
