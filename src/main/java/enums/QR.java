package enums;

public enum QR {
	REQUEST((boolean) false), REPLY((boolean) true);

	public boolean code;

	private QR(boolean code) {
		this.code = code;
	}

	public static QR getTypeByCode(boolean code) {
		for (QR e : QR.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}
}
