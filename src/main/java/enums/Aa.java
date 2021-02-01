package enums;

public enum Aa {
	AUTHORITATIVE((boolean) true),
	NON_AUTHORITATIVE((boolean) false);
	
	public boolean code;
	
	private Aa(boolean code) {
		this.code=code;
	}
	
    public static Aa getTypeByCode(boolean code){
        for(Aa e :Aa.values()){
            if(e.code == code) return e;
        }
        return null;
    }
}
