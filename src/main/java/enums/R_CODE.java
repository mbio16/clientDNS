package enums;

public enum R_CODE {
	NO_ERROR((byte) 0x00), ERROR_IN_QUERRY((byte) 0x01), SERVER_ERROR((byte) 0x02), NO_AUTH_FOR_ZONE((byte) 0x03),
	NOT_IMPLEMENTED((byte) 0x04), REQUEST_DENIED((byte) 0x05);

	public byte code;
	public String message;

	private R_CODE(Byte code) {
		this.code = code;
	}

	public static R_CODE getTypeByCode(byte code) {
		for (R_CODE e : R_CODE.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}

}
