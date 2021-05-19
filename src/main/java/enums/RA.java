package enums;

public enum RA {
	RECURSION_AVAIBLE((boolean) true), RECURSION_NON_AVAIBLE((boolean) false);

	public boolean code;

	private RA(boolean code) {
		this.code = code;
	}

	public static RA getTypeByCode(boolean code) {
		for (RA e : RA.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}

}
