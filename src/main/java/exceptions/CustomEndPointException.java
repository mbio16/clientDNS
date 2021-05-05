package exceptions;

public class CustomEndPointException extends Exception {

	private static final long serialVersionUID = 1L;

	public CustomEndPointException() {
		super("End point is not valid");
	}
}
