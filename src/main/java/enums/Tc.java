package enums;

public enum Tc {
	FRANGMENTED((boolean) true),
	NON_FRAGMENTED((boolean) false);
	
	public boolean code;
	
	private Tc(boolean code) {
		this.code= code;
	}
	
    public static Tc getTypeByCode(boolean code){
        for(Tc e :Tc.values()){
            if(e.code == code) return e;
        }
        return null;
    }
	
}
