package records;

import org.json.simple.JSONObject;
import enums.DIGEST_TYPE;
import enums.Q_COUNT;
import models.UInt16;

public class RecordNSEC3 extends RecordNSEC {

	private DIGEST_TYPE hash;
	private byte flags;
	private UInt16 iteration;
	private int saltLenght;
	private String salt;
	private int hashLenght;
	private String name;
	private static final String KEY_HASH_TYPE = "HASH_TYPE";
	private static final String KEY_FLAGS = "FLAGS";
	private static final String KEY_ITERATION = "ITERATIONS";
	private static final String KEY_SALT = "SALT";
	private static final String KEY_SALT_LENGHT = "SALT_LENGHT";
	private static final String KEY_HASH_LENGHT = "HASH_LENGHT";
	private static final String KEY_NEXT_OWNER_HASH = "NEXT_DOMAIN_HASH";

	public RecordNSEC3(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex, true);
		salt = "";
		name = "";
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

		currentIndex += saltLenght;
		hashLenght = (int) rawMessage[currentIndex];
		currentIndex += 1;
		for (int i = currentIndex; i < currentIndex + hashLenght; i++) {
			name += String.format("%02x", rawMessage[i]);
		}
		currentIndex = currentIndex + hashLenght;
		parseTypeBits(currentIndex);

	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public String getDataAsString() {

		return "";
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
		object.put(KEY_HASH_LENGHT, hashLenght);
		object.put(KEY_NEXT_OWNER_HASH, name);
		object.put(KEY_TYPE_BITS, recordsTypes);
		return object;
	}

	@Override
	public String[] getValesForTreeItem() {
		String[] response = new String[recordsTypes.size() + 7];

		response[0] = KEY_HASH_TYPE + ": " + hash;
		response[1] = KEY_FLAGS + ": " + flags;
		response[2] = KEY_ITERATION + ": " + iteration.getValue();
		response[3] = KEY_SALT + ": " + salt;
		response[4] = KEY_SALT_LENGHT + ": " + saltLenght;
		response[5] = KEY_HASH_LENGHT + ": " + hashLenght;
		response[6] = KEY_NEXT_OWNER_HASH + ": " + name;
		int i = 7;
		for (Q_COUNT count : recordsTypes) {
			response[i] = KEY_TYPE_BIT + ": " + count;
			i++;
		}
		return response;
	}

	@Override
	public String getDataForTreeViewName() {
		return hash + " " + flags + " " + iteration + " ...";
	}
}
