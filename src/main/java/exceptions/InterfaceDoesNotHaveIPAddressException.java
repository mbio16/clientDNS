package exceptions;

public class InterfaceDoesNotHaveIPAddressException extends Exception {
	private static final long serialVersionUID = 1L;

	public InterfaceDoesNotHaveIPAddressException() {
		super("Network Interface does not have IP address");
	}
}
