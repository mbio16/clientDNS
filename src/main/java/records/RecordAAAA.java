package records;

import java.net.UnknownHostException;

import org.json.simple.JSONObject;

public class RecordAAAA extends RecordA {
	private static final String KEY_ADDRESS = "Ipv6";

	public RecordAAAA(byte[] rawMessage, int lenght, int startIndex) throws UnknownHostException {
		super(rawMessage, lenght, startIndex);
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_ADDRESS, ipAddressAsString);
		return object;
	}

}
