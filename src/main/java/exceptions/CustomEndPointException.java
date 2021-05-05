package exceptions;

public class CustomEndPointException extends Exception {

	public CustomEndPointException() {
		super("End point is not valid");
	}
}
