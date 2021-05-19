package records;

import org.json.simple.JSONObject;
import enums.CERTIFICATE_FLAG;

public class RecordCAA extends Record {
	private CERTIFICATE_FLAG flag;
	private int tagLenght;
	private String tag;
	private String value;
	private static final String KEY_CERTIFICATE_FLAG = "Flag";
	private static final String KEY_TAG = "Tag";
	private static final String KEY_VALUE = "Value";

	public RecordCAA(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		tag = "";
		value = "";
		parse();
	}

	private void parse() {
		flag = CERTIFICATE_FLAG.getTypeByCode(rawMessage[startIndex]);
		tagLenght = (int) rawMessage[startIndex + 1];
		int currentIndex = startIndex + 2;
		for (int i = currentIndex; i < currentIndex + tagLenght; i++) {
			tag += (char) rawMessage[i];
		}
		currentIndex += tagLenght;

		for (int i = currentIndex; i < startIndex + lenght; i++) {
			value += (char) rawMessage[i];
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJson() {
		JSONObject json = new JSONObject();
		json.put(KEY_CERTIFICATE_FLAG, flag);
		json.put(KEY_TAG, tag);
		json.put(KEY_VALUE, value);
		return json;
	}

	@Override
	public String toString() {
		return KEY_CERTIFICATE_FLAG + ": " + flag + "\n " + KEY_TAG + ": " + ": " + tag + "\n" + KEY_VALUE + ": " + ": "
				+ value;
	}

	@Override
	public String[] getValesForTreeItem() {
		String[] pole = { KEY_CERTIFICATE_FLAG + ": " + flag, KEY_TAG + ": " + tag, KEY_VALUE + ": " + value };
		return pole;
	}

	@Override
	public String getDataForTreeViewName() {
		return flag + " " + tag + " " + value;
	}
}
