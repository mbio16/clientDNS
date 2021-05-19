package enums;

public enum KEY_USAGE {
	KEY_SIGNING_KEY((byte) 0x01), NOT_KEY_SIGNING_KEY((byte) 0x00);

	public byte code;

	private KEY_USAGE(byte code) {
		this.code = code;
	}

	public static KEY_USAGE getTypeByCode(byte code) {
		for (KEY_USAGE e : KEY_USAGE.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}
}
