package enums;

public enum AA {
	AUTHORITATIVE((boolean) true), NON_AUTHORITATIVE((boolean) false);

	public boolean code;

	private AA(boolean code) {
		this.code = code;
	}

	public static AA getTypeByCode(boolean code) {
		for (AA e : AA.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}
}
