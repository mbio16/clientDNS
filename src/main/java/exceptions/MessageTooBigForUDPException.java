package exceptions;

public class MessageTooBigForUDPException extends Exception {

	private static final long serialVersionUID = 1L;

	public MessageTooBigForUDPException() {
		super("Message has more than 512 bytes, too big for UDP message.");
	}
}
