package exceptions;

public class NotValidDomainNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotValidDomainNameException() {
		super("Domain name is not in valid format");
	}

}
