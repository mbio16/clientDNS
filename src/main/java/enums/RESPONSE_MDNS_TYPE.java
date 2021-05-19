package enums;

public enum RESPONSE_MDNS_TYPE {

	RESPONSE_MULTICAST(false, 0), RESPONSE_UNICAST(true, 32768);

	public boolean code;
	public int value;

	private RESPONSE_MDNS_TYPE(boolean code, int value) {
		this.code = code;
		this.value = value;
	}

	public static RESPONSE_MDNS_TYPE getTypeByCode(boolean code) {
		if (code) {
			return RESPONSE_UNICAST;
		} else {
			return RESPONSE_MULTICAST;
		}
	}
}
