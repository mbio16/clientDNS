package records;

import java.io.UnsupportedEncodingException;
import org.json.simple.JSONObject;

public class RecordTXT extends Record {

	private static final String KEY_TEXT = "Text";
	private String stringText;

	public RecordTXT(byte[] rawMessage, int lenght, int startIndex) throws UnsupportedEncodingException {
		super(rawMessage, lenght, startIndex);
		parse();
	}

	public void parse() throws UnsupportedEncodingException {
		byte[] textByte = new byte[lenght - 1];
		int j = 0;
		for (int i = startIndex + 1; i < startIndex + lenght; i++) {
			textByte[j] = rawMessage[i];
			j++;
		}
		// haS TO BE REAPAIRED
		stringText = new String(textByte, "UTF-8");

	}

	@Override
	public String getDataAsString() {
		return stringText;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_TEXT, stringText);
		return object;
	}

	@Override
	public String[] getValesForTreeItem() {
		String[] pole = { KEY_TEXT + ": " + stringText };
		return pole;
	}

	@Override
	public String getDataForTreeViewName() {
		return stringText;
	}
}
