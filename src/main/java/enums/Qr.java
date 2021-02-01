package enums;

public enum Qr {
	REQUEST((boolean) false),
	REPLY((boolean) true);
	
	public boolean code;
	
    private Qr(boolean code) {
        this.code = code;
    }
    
    public static Qr getTypeByCode(boolean code){
        for(Qr e :Qr.values()){
            if(e.code == code) return e;
        }
        return null;
    }
}
