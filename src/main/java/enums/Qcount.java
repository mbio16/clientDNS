package enums;

import models.UInt16;

public enum Qcount {
	    A(1),
	    AAAA(28),
		CDNSKEY(60),
		CNAME(5),
		DNSKEY(48),
		DS(39),
		PTR(12),
		SOA(6),
		MX(15),
		RRSIG(46),
		SIG(24),
		TXT(16),
		CAA(257),
		CERT(37);
		
		public UInt16 code;
	    private Qcount(UInt16 code) {
	        this.code = code;
	    }
	    private Qcount(int code) {
	    	this.code = new UInt16(code);
	    }
	    public static Qcount getTypeByCode(UInt16 code){
	        for(Qcount type : Qcount.values()){
	            if(type.code.equals(code)) return type;
	        }
	        return null;
	    }
	    
	}
