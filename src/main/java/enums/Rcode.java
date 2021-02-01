package enums;

public enum Rcode {
    NO_ERROR((byte) 0x00),
    ERROR_IN_QUERRY((byte) 0x01),
    SERVER_ERROR((byte) 0x02),
    NO_AUTH_FOR_ZONE((byte) 0x03),
	NOT_IMPLEMENTED((byte) 0x04),
	REQUEST_DENIED((byte) 0x05);
	
	public byte code;
	public String message;
	
    private Rcode(Byte code) {
        this.code = code;
    }
	
        public static Rcode getTypeByCode(byte code){
            for(Rcode e :Rcode.values()){
                if(e.code == code) return e;
            }
            return null;
        }
    	

}
