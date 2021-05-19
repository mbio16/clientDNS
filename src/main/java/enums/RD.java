package enums;

public enum RD {
	RECURSIVE((boolean) true), ITERATIVE((boolean) false);

	public boolean code;

	private RD(boolean code) {
		this.code = code;
	}

	public static RD getTypeByCode(boolean code) {
		for (RD e : RD.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}

}
