package records;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;

import enums.DNSSEC_ALGORITHM_TYPE;
import enums.Q_COUNT;
import models.DomainConvert;
import models.UInt16;

public class RecordRRSIG extends Record {

	private Q_COUNT qcount;
	private DNSSEC_ALGORITHM_TYPE algorithmType;
	private int label;
	private int orriginalTTL;
	private Date signatureExpiration;
	private Date signatureInception;
	private UInt16 keyTag;
	private String name;
	private String signature;
	private static final String KEY_QCOUNT = "Type";
	private static final String KEY_ALGORITHM = "Algorithm";
	private static final String KEY_LABEL = "Label";
	private static final String KEY_TTL = "Original TTL";
	private static final String KEY_EXPIRATION = "Signature expiration";
	private static final String KEY_INCEPTION = "Signature inception";
	private static final String KEY_TAG = "Key tag";
	private static final String KEY_NAME = "Signature name";
	private static final String KEY_SIGNATURE = "Signature";
	private static final String TIME_FORMAT = "dd-MM-yyyy hh:mm:ss";

	public RecordRRSIG(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		name = "";
		signature = "";
		parse();
	}

	private void parse() {
		int currentIndex = startIndex;
		qcount = Q_COUNT
				.getTypeByCode(new UInt16().loadFromBytes(rawMessage[currentIndex], rawMessage[currentIndex + 1]));
		currentIndex += 2;

		algorithmType = DNSSEC_ALGORITHM_TYPE.getTypeByCode(rawMessage[currentIndex]);
		currentIndex += 1;

		label = (int) currentIndex;
		currentIndex += 1;

		orriginalTTL = ByteBuffer.wrap(get4bytes(currentIndex)).getInt();
		currentIndex += 4;

		signatureExpiration = new Date((long) ByteBuffer.wrap(get4bytes(currentIndex)).getInt() * 1000);
		currentIndex += 4;

		signatureInception = new Date((long) ByteBuffer.wrap(get4bytes(currentIndex)).getInt() * 1000);
		currentIndex += 4;

		keyTag = new UInt16().loadFromBytes(rawMessage[currentIndex], rawMessage[currentIndex + 1]);
		currentIndex += 2;

		name = DomainConvert.decodeDNS(rawMessage, currentIndex);
		currentIndex = DomainConvert.getIndexOfLastByteOfName(rawMessage, currentIndex) + 1;

		for (int i = currentIndex; i < startIndex + lenght; i++) {
			signature += String.format("%02x", rawMessage[i]);
		}

	}

	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		DateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
		JSONObject object = new JSONObject();
		object.put(KEY_QCOUNT, qcount);
		object.put(KEY_ALGORITHM, algorithmType);
		object.put(KEY_LABEL, label);
		object.put(KEY_TTL, orriginalTTL);
		object.put(KEY_EXPIRATION, dateFormat.format(signatureExpiration));
		object.put(KEY_INCEPTION, dateFormat.format(signatureInception));
		object.put(KEY_TAG, keyTag.getValue());
		object.put(KEY_NAME, name);
		object.put(KEY_SIGNATURE, signature);
		return object;
	}

	@Override
	public String[] getValesForTreeItem() {
		DateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
		String[] pole = { KEY_QCOUNT + ": " + qcount, KEY_ALGORITHM + ": " + algorithmType, KEY_LABEL + ": " + label,
				KEY_TTL + ": " + orriginalTTL, KEY_EXPIRATION + ": " + dateFormat.format(signatureExpiration),
				KEY_INCEPTION + ": " + dateFormat.format(signatureInception), KEY_TAG + ": " + keyTag.getValue(),
				KEY_NAME + ": " + name, KEY_SIGNATURE + ": " + signature };
		return pole;
	}

	@Override
	public String getDataForTreeViewName() {
		return algorithmType + " " + qcount + " " + label + " ...";
	}
}
