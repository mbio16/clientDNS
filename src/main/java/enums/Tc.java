package enums;

public enum TC {
	FRANGMENTED((boolean) true),
	NON_FRAGMENTED((boolean) false);
	
	public boolean code;
	
	private TC(boolean code) {
		this.code= code;
	}
	
    public static TC getTypeByCode(boolean code){
        for(TC e :TC.values()){
            if(e.code == code) return e;
        }
        return null;
    }
	
}
