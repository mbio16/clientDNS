package records;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import enums.Q_COUNT;
import models.DataTypesConverter;
import models.DomainConvert;
import models.UInt16;

public class RecordNSEC extends Record {
	protected String name;
	protected ArrayList<Q_COUNT> recordsTypes;
	protected static final String KEY_NAME = "NEXT_DOMAIN_NAME";
	protected static final String KEY_TYPE_BIT = "RECORD_IN_DOMAIN";
	protected static final String KEY_TYPE_BITS = "RECORDS_IN_DOMAIN";

	public RecordNSEC(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		recordsTypes = new ArrayList<Q_COUNT>();
		parseRecord();
	}

	public RecordNSEC(byte[] rawMessage, int lenght, int startIndex, boolean nsec3) {
		super(rawMessage, lenght, startIndex);
		recordsTypes = new ArrayList<Q_COUNT>();
	}

	private void parseRecord() {
		name = DomainConvert.decodeDNS(rawMessage, startIndex);
		int currentIndex = DomainConvert.getIndexOfLastByteOfName(rawMessage, startIndex) + 1;
		parseTypeBits(currentIndex);
	}

	protected void parseTypeBits(int currentIndex) {
		while (currentIndex < startIndex + lenght - 1) {
			if (rawMessage[currentIndex] == (byte) 0x00) {
				currentIndex = parseBits(currentIndex + 1, 0);
			} else {
				currentIndex = parseBits(currentIndex + 1, 256);
			}
		}
		// System.out.println(recordsTypes.toString());
	}

	protected int parseBits(int currentIndex, int startValue) {
		int lenght = (int) rawMessage[currentIndex];
		currentIndex++;
		int value = startValue;
		for (int i = currentIndex; i < currentIndex + (lenght); i++) {
			boolean bits[] = DataTypesConverter.byteToBoolArr(rawMessage[i], 8);
			for (int j = bits.length - 1; j >= 0; j--) {
				if (bits[j]) {
					recordsTypes.add(Q_COUNT.getTypeByCode(new UInt16(value)));
				}
				value++;
			}
		}
		return currentIndex + lenght;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_NAME, name);
		object.put(KEY_TYPE_BITS, recordsTypes);
		return object;
	}

	@Override
	public String[] getValesForTreeItem() {
		String[] response = new String[recordsTypes.size() + 1];

		response[0] = (KEY_NAME + ": " + name);
		int i = 1;
		for (Q_COUNT count : recordsTypes) {
			response[i] = (KEY_TYPE_BIT + ": " + count.toString());
			i++;
		}
		return response;
	}

	@Override
	public String getDataForTreeViewName() {
		return name + " ...";
	}

}
