package enums;

public enum KEY_TYPE {
	DNS_ZONE_KEY((byte) 0x01), NOT_DNS_ZONE_KEY((byte) 0x00);

	public byte code;

	private KEY_TYPE(byte code) {
		this.code = code;
	}

	public static KEY_TYPE getTypeByCode(byte code) {
		for (KEY_TYPE e : KEY_TYPE.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}

}
