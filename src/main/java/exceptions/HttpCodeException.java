package exceptions;

public class HttpCodeException extends Exception {
		private static final long serialVersionUID = 1L;

		public HttpCodeException(int code) {
			super("" + code);
		}
}
