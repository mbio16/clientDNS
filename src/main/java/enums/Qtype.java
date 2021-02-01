package enums;

import models.UInt16;

public enum Qtype {
	IN(1);
	public UInt16 code;
    private Qtype(UInt16 code) {
        this.code = code;
    }
    
    private Qtype(int code) {
    	this.code = new UInt16(code);
    }
    
    public static Qtype getTypeByCode(UInt16 code){
        for(Qtype e : Qtype.values()){
            if(e.code.equals(code)) return e;
        }
        return null;
    }
}
