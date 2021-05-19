package enums;

public enum DNSSEC_ALGORITHM_TYPE {
	RESERVED((byte) 0x00), RSA_MD5((byte) 0x01), DH((byte) 0x02), DSA((byte) 0x03), RESERVED2((byte) 0x04),
	RSA_SHA1((byte) 0x05), DSA_NSEC3_SHA1((byte) 0x06), RSASHA1_NSEC3_SHA1((byte) 0x07), RSA_SHA256((byte) 0x08),
	RESERVED3((byte) 0x09), RSA_SHA512((byte) 0x0a), ECC_GOST((byte) 0x0b), ECDSA_P256_SHA256((byte) 0x0c),
	ECDSA_P384_SHA384((byte) 0x0d), ED25519((byte) 0x0e), ED448((byte) 0x0f);

	private byte code;

	private DNSSEC_ALGORITHM_TYPE(byte code) {
		this.code = code;
	}

	public static DNSSEC_ALGORITHM_TYPE getTypeByCode(byte code) {
		for (DNSSEC_ALGORITHM_TYPE e : DNSSEC_ALGORITHM_TYPE.values()) {
			if (e.code == code)
				return e;
		}
		return null;
	}
}
