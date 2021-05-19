package exceptions;

public class ResponseDoesNotContainRequestDomainNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public ResponseDoesNotContainRequestDomainNameException() {
		super("Response is not for the request");
	}
}
