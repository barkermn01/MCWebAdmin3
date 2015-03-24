package MCWebAdmin.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import MCWebAdmin.Config.Serializable.Backups;
import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Instance.InstanceManager;
import MCWebAdmin.Util.Exceptions.ServerDoesNotExist;
import MCWebAdmin.Util.Exceptions.ServerIsRunning;

public class Backup {
	private String serverName, backupName, created;
	
	public Backup(String server, String backup){
		serverName = server;
		backupName = backup;
	}
	
	public String GetPath()
	{
		return Global.GetInstance().InstanceBackupPath+serverName+"_"+backupName+".zip";
	}
	
	private void addDir(File dirObj, ZipOutputStream out) throws IOException {
	    File[] files = dirObj.listFiles();
	    byte[] tmpBuf = new byte[1024];

	    for (int i = 0; i < files.length; i++) {
	      if (files[i].isDirectory()) {
	        addDir(files[i], out);
	        continue;
	      }
	      FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
	      System.out.println(" Adding: " + files[i].getAbsolutePath());
	      out.putNextEntry(new ZipEntry(files[i].getAbsolutePath()));
	      int len;
	      while ((len = in.read(tmpBuf)) > 0) {
	        out.write(tmpBuf, 0, len);
	      }
	      out.closeEntry();
	      in.close();
	    }
	  }
	
	public void Create()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		created = dateFormat.format(date);
		String dir = Global.GetInstance().InstancesPath+Server.GetServerInstance(serverName).name+"/";
		File dirObj = new File(dir);
		ZipOutputStream out;
		try {
			out = new ZipOutputStream(new FileOutputStream(GetPath()));
		    addDir(dirObj, out);
		    out.close();
		    Backups.getInstance().AddBackup(this);
		} catch (Exception e) {
		}
	}
	
	public void Delete()
	{
		File f = new File(GetPath());
		f.delete();
	    Backups.getInstance().RemoveBackup(this);
	}
	
	public void Restore()
	{
		try {
			InstanceManager.GetInstance().StopInstance(serverName);
			InstanceManager.GetInstance().UninstallFiles(serverName);
		} catch (ServerDoesNotExist | ServerIsRunning e) {
		}
		String outDir = Global.GetInstance().InstancesPath+Server.GetServerInstance(serverName).name+"/";
		try{
		byte[] buf = new byte[1024];
	    ZipInputStream zipinputstream = null;
	    ZipEntry zipentry;
	    zipinputstream = new ZipInputStream(new FileInputStream(GetPath()));
	    zipentry = zipinputstream.getNextEntry();
	    while (zipentry != null) {
	      String entryName = zipentry.getName();
	      FileOutputStream fileoutputstream;
	      File newFile = new File(entryName);
	      String directory = newFile.getParent();

	      if (directory == null) {
	        if (newFile.isDirectory())
	          break;
	      }
	      fileoutputstream = new FileOutputStream(outDir + entryName);
	      int n;
	      while ((n = zipinputstream.read(buf, 0, 1024)) > -1){
	        fileoutputstream.write(buf, 0, n);
	      }
	      fileoutputstream.close();
	      zipinputstream.closeEntry();
	      zipentry = zipinputstream.getNextEntry();
	    }
	    zipinputstream.close();
		}catch(Exception e){
			System.out.println("Restoration of backup '"+GetPath()+"' failed!");
		}
	}
}
