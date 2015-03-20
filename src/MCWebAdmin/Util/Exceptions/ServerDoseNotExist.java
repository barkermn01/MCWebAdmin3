package MCWebAdmin.Util.Exceptions;

public class ServerDoseNotExist extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerDoseNotExist() {
        super("There is no server by that name saved");
    }
}
