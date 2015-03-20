package MCWebAdmin.Util.Exceptions;

public class PortInUse extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PortInUse(String Service){
		super("Failed to start server '"+Service+"' as the port is bound somewhere else");
	}
	
}
