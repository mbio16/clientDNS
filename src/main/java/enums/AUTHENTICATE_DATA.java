package enums;

public enum AUTHENTICATE_DATA {
	NON_AUTHENTICATED_ACCEPTED((boolean) true), NON_AUTHENTICATED_NOT_ACCEPTED((boolean) false);

	public boolean code;

	private AUTHENTICATE_DATA(boolean code) {
		this.code = code;
	}

	public static AUTHENTICATE_DATA getTypeByCode(boolean code) {
		for (AUTHENTICATE_DATA e : AUTHENTICATE_DATA.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}

}
