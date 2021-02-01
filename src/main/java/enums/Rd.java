package enums;


public enum Rd {
	RECURSIVE((boolean) true),
	ITERATIVE((boolean) false);
	
	public boolean code;
	
	private Rd(boolean code) {
		this.code= code;
	}
	
    public static Rd getTypeByCode(boolean code){
        for(Rd e :Rd.values()){
            if(e.code == code) return e;
        }
        return null;
    }
	
}

