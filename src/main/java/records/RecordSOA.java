package records;

import java.nio.ByteBuffer;

import org.json.simple.JSONObject;

import models.DomainConvert;

public class RecordSOA extends Record {

	private String mname;
	private String rname;
	private int serial;
	private int refresh;
	private int retry;
	private int expire;
	private int minimun;

	private static final String KEY_MNAME = "Domain";
	private static final String KEY_RNAME = "Responsible";
	private static final String KEY_SERIAL = "Serial";
	private static final String KEY_REFRESH = "Refresh";
	private static final String KEY_RETRY = "Retry";
	private static final String KEY_EXPIRY = "Expiry";
	private static final String KEY_MINIMUM = "Minimum";

	public RecordSOA(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		parse();
	}

	private void parse() {
		mname = DomainConvert.decodeDNS(rawMessage, startIndex);
		int currentIndex = DomainConvert.getIndexOfLastByteOfName(rawMessage, startIndex) + 1;
		rname = DomainConvert.decodeDNS(rawMessage, currentIndex);
		currentIndex = DomainConvert.getIndexOfLastByteOfName(rawMessage, currentIndex) + 1;
		byte[] dataBytes = { rawMessage[currentIndex], rawMessage[currentIndex + 1], rawMessage[currentIndex + 2],
				rawMessage[currentIndex + 3] };
		serial = ByteBuffer.wrap(dataBytes).getInt();
		currentIndex += 4;

		byte[] dataBytes1 = { rawMessage[currentIndex], rawMessage[currentIndex + 1], rawMessage[currentIndex + 2],
				rawMessage[currentIndex + 3] };
		refresh = ByteBuffer.wrap(dataBytes1).getInt();
		currentIndex += 4;

		byte[] dataBytes2 = { rawMessage[currentIndex], rawMessage[currentIndex + 1], rawMessage[currentIndex + 2],
				rawMessage[currentIndex + 3] };
		retry = ByteBuffer.wrap(dataBytes2).getInt();
		currentIndex += 4;

		byte[] dataBytes3 = { rawMessage[currentIndex], rawMessage[currentIndex + 1], rawMessage[currentIndex + 2],
				rawMessage[currentIndex + 3] };
		expire = ByteBuffer.wrap(dataBytes3).getInt();
		currentIndex += 4;

		byte[] dataBytes4 = { rawMessage[currentIndex], rawMessage[currentIndex + 1], rawMessage[currentIndex + 2],
				rawMessage[currentIndex + 3] };
		minimun = ByteBuffer.wrap(dataBytes4).getInt();
	}

	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_MNAME, mname);
		object.put(KEY_RNAME, rname);
		object.put(KEY_SERIAL, serial);
		object.put(KEY_REFRESH, refresh);
		object.put(KEY_RETRY, retry);
		object.put(KEY_EXPIRY, expire);
		object.put(KEY_MINIMUM, minimun);
		return object;
	}

	@Override
	public String[] getValesForTreeItem() {
		String[] pole = { KEY_MNAME + ": " + mname, KEY_RNAME + ": " + rname, KEY_SERIAL + ": " + serial,
				KEY_REFRESH + ": " + refresh, KEY_RETRY + ": " + retry, KEY_EXPIRY + ": " + expire,
				KEY_MINIMUM + ": " + minimun };
		return pole;
	}

	@Override
	public String getDataForTreeViewName() {
		return mname + " " + rname + " " + " ...";
	}
}
