package exceptions;

public class QueryIdNotMatchException extends Exception {

	private static final long serialVersionUID = 1L;

	public QueryIdNotMatchException() {
		super("Query does not match exception");
	}
}
