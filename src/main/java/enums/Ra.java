package enums;

public enum Ra {
	RECURSION_AVAIBLE((boolean) true),
	RECURSION_NON_AVAIBLE((boolean) false);
	
	public boolean code;
	
	private Ra(boolean code) {
		this.code= code;
	}
	
    public static Ra getTypeByCode(boolean code){
        for(Ra e :Ra.values()){
            if(e.code == code) return e;
        }
        return null;
    }
	
}
