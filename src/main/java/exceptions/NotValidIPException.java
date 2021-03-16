package exceptions;

public class NotValidIPException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotValidIPException() {
		super("Ip is not valid");
	}
}
