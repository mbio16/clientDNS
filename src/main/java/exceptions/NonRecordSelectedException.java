package exceptions;

public class NonRecordSelectedException extends Exception{

	public NonRecordSelectedException() {
		super("Non record for query selected");
	}
}
