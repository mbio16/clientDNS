package enums;

import models.UInt16;

public enum Qcount {
	    A(1), //done
	    AAAA(28), //done
		CNAME(5), //done
		DNSKEY(48),
		DS(43),
		PTR(12),
		SOA(6),
		MX(15),
		RRSIG(46),
		SIG(24),
		TXT(16),
		CAA(257),
		CERT(37),
		NS(2);
		
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
