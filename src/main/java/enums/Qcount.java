package enums;

public enum Qcount {
	    A((byte)1),
	    AAAA((byte) 28),
		CDNSKEY((byte) 60),
		CNAME((byte) 5),
		DNSKEY((byte)48),
		DS((byte) 39),
		PTR((byte) 12),
		SOA((byte) 6),
		MX((byte) 15),
		RRSIG((byte) 46),
		SIG((byte) 24),
		TXT((byte) 16),
		CAA((byte) 257),
		CERT((byte) 37);
		
		public byte code;
	    private Qcount(Byte code) {
	        this.code = code;
	    }
	}
