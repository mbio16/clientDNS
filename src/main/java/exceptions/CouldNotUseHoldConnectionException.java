package exceptions;

public class CouldNotUseHoldConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	public CouldNotUseHoldConnectionException() {
		super("Could not use TCP connection");
	}

}
