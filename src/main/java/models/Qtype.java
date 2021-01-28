package models;

public enum Qtype {
	IN((byte) 1);
	public byte code;
    private Qtype(Byte code) {
        this.code = code;
    }
}
