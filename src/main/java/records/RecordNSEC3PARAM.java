package records;

import org.json.simple.JSONObject;

import enums.DIGEST_TYPE;
import models.UInt16;

public class RecordNSEC3PARAM extends Record {

	private DIGEST_TYPE hash;
	private UInt16 iteration;
	private int saltLenght;
	private byte flags;
	private String salt;
	private static final String KEY_HASH_TYPE = "HASH_TYPE";
	private static final String KEY_FLAGS = "FLAGS";
	private static final String KEY_ITERATION = "ITERATIONS";
	private static final String KEY_SALT = "SALT";
	private static final String KEY_SALT_LENGHT = "SALT_LENGHT";

	public RecordNSEC3PARAM(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		salt = "";
		parseRecord();
	}

	private void parseRecord() {
		hash = DIGEST_TYPE.getTypeByCode(rawMessage[startIndex]);
		flags = rawMessage[startIndex + 1];
		int currentIndex = startIndex + 2;

		iteration = new UInt16().loadFromBytes(rawMessage[currentIndex], rawMessage[currentIndex + 1]);
		currentIndex += 2;
		saltLenght = (int) rawMessage[currentIndex];
		currentIndex += 1;
		for (int i = currentIndex; i < currentIndex + saltLenght; i++) {
			salt += String.format("%02x", rawMessage[i]);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_HASH_TYPE, hash);
		object.put(KEY_FLAGS, (int) flags);
		object.put(KEY_ITERATION, iteration.getValue());
		object.put(KEY_SALT, salt);
		object.put(KEY_SALT_LENGHT, saltLenght);
		return object;
	}

	@Override
	public String[] getValesForTreeItem() {
		String response[] = new String[] { KEY_HASH_TYPE + ": " + hash, KEY_FLAGS + ": " + flags,
				KEY_ITERATION + ": " + iteration.getValue(), KEY_SALT + ": " + salt,
				KEY_SALT_LENGHT + ": " + saltLenght, };

		return response;
	}

	@Override
	public String getDataForTreeViewName() {
		return hash + " " + flags + " " + iteration + " ...";
	}
}
