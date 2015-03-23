package MCWebAdmin.Util.Exceptions;

public class ServerNameInUse extends Exception {

		public ServerNameInUse(String name){
			super("The server name '"+name+"' is used");
		}
}
