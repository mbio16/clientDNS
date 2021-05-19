package enums;

public enum CERTIFICATE_FLAG {
	ISSUER((byte) 0x80);

	private byte code;

	private CERTIFICATE_FLAG(byte code) {
		this.code = code;
	}

	public static CERTIFICATE_FLAG getTypeByCode(byte code) {
		for (CERTIFICATE_FLAG type : CERTIFICATE_FLAG.values()) {
			if (type.code == code)
				return type;
		}
		return null;
	}
}