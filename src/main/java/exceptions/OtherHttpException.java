package exceptions;

public class OtherHttpException extends Exception {
	private static final long serialVersionUID = 1L;

	public OtherHttpException() {
		super("Unknown http exception");
	}
}
