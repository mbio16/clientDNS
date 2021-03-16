package records;

import org.json.simple.JSONObject;

public class RecordNS extends RecordCNAME {

	private static String KEY_NAMESERVER = "NameServer";

	public RecordNS(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_NAMESERVER, name);
		return object;
	}

	@Override
	public String[] getValesForTreeItem() {
		String[] pole = { KEY_NAMESERVER + ": " + name, };
		return pole;
	}

	@Override
	public String getDataForTreeViewName() {
		return name;
	}
}
