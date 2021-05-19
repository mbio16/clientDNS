package records;

import org.json.simple.JSONObject;

import models.DomainConvert;
import models.UInt16;

public class RecordSRV extends Record {

	private UInt16 priority;
	private UInt16 weight;
	private UInt16 port;
	private String target;
	private static final String KEY_PRIORITY = "Priority";
	private static final String KEY_WEIGHT = "Weight";
	private static final String KEY_PORT = "Port";
	private static final String KEY_TARGET = "Target";

	public RecordSRV(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		parseRecord();
	}

	private void parseRecord() {
		priority = new UInt16().loadFromBytes(rawMessage[startIndex], rawMessage[startIndex + 1]);
		int curentIndex = startIndex + 2;
		weight = new UInt16().loadFromBytes(rawMessage[curentIndex], rawMessage[curentIndex + 1]);
		curentIndex += 2;
		port = new UInt16().loadFromBytes(rawMessage[curentIndex], rawMessage[curentIndex + 1]);
		curentIndex += 2;
		target = DomainConvert.decodeMDNS(rawMessage, curentIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_PRIORITY, priority.getValue());
		object.put(KEY_WEIGHT, weight.getValue());
		object.put(KEY_PORT, port.getValue());
		object.put(KEY_TARGET, target);
		return object;
	}

	@Override
	public String[] getValesForTreeItem() {
		String[] pole = { KEY_PRIORITY + ": " + priority.getValue(), KEY_WEIGHT + ": " + weight.getValue(),
				KEY_PORT + ": " + port.getValue(), KEY_TARGET + ": " + target };
		return pole;
	}

	@Override
	public String getDataForTreeViewName() {
		return priority.getValue() + " " + weight.getValue() + " " + port.getValue() + "...";
	}

}
