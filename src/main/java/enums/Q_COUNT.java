package enums;

import models.UInt16;

public enum Q_COUNT {
	A(1), AAAA(28), CNAME(5), DNSKEY(48), DS(43), PTR(12), SOA(6), MX(15), RRSIG(46), SIG(24), TXT(16), CAA(257),
	CERT(37), OPT(41), NS(2), NSEC3(50), NSEC(47), NSEC3PARAM(51), ANY(255), SRV(33);

	public UInt16 code;

	private Q_COUNT(UInt16 code) {
		this.code = code;
	}

	private Q_COUNT(int code) {
		this.code = new UInt16(code);
	}

	public static Q_COUNT getTypeByCode(UInt16 code) {
		for (Q_COUNT type : Q_COUNT.values()) {
			if (type.code.equals(code))
				return type;
		}
		return null;
	}

}
