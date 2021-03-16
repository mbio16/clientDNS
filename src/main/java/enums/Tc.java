package enums;

public enum TC {
	TRANCATED((boolean) true),
	NON_TRANCATED((boolean) false);

	public boolean code;

	private TC(boolean code) {
		this.code = code;
	}

	public static TC getTypeByCode(boolean code) {
		for (TC e : TC.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}

}
