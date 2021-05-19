package enums;

public enum CHECKING_DISABLED {
	DATA_NOT_CHECKED((boolean) true), DATA_CHECKED((boolean) false);

	public boolean code;

	private CHECKING_DISABLED(boolean code) {
		this.code = code;
	}

	public static CHECKING_DISABLED getTypeByCode(boolean code) {
		for (CHECKING_DISABLED e : CHECKING_DISABLED.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}

}
