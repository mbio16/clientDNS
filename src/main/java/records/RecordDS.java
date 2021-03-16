package records;

import org.json.simple.JSONObject;

import enums.DIGEST_TYPE;
import enums.DNSSEC_ALGORITHM_TYPE;
import models.UInt16;

public class RecordDS extends Record {

	private UInt16 keyTag;
	private DNSSEC_ALGORITHM_TYPE algorithmType;
	private DIGEST_TYPE digestType;
	private String hash;

	private static final String KEY_TAG = "Key tag";
	private static final String KEY_ALGORITHM_TYPE = "Algorithm type";
	private static final String KEY_DIGEST_TYPE = "Digest type";
	private static final String KEY_HASH = "Key hash";

	public RecordDS(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		hash = "";
		parse();
	}

	public void parse() {
		keyTag = new UInt16().loadFromBytes(rawMessage[startIndex], rawMessage[startIndex + 1]);
		int currentIndex = startIndex + 2;
		algorithmType = DNSSEC_ALGORITHM_TYPE.getTypeByCode(rawMessage[currentIndex]);
		currentIndex++;
		digestType = DIGEST_TYPE.getTypeByCode(rawMessage[currentIndex]);
		currentIndex++;
		for (int i = currentIndex; i < startIndex + lenght; i++) {
			hash += String.format("%02x", rawMessage[i]);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_TAG, keyTag.getValue());
		object.put(KEY_ALGORITHM_TYPE, algorithmType);
		object.put(KEY_DIGEST_TYPE, digestType);
		object.put(KEY_HASH, hash);
		return object;
	}

	@Override
	public String[] getValesForTreeItem() {
		String[] pole = { KEY_TAG + ": " + keyTag.getValue(), KEY_ALGORITHM_TYPE + ": " + algorithmType,
				KEY_DIGEST_TYPE + ": " + digestType, KEY_HASH + ": " + hash };
		return pole;
	}

	@Override
	public String getDataForTreeViewName() {
		return algorithmType + " " + digestType + " " + " ...";
	}
}
