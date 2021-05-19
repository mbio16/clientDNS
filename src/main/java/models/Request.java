package models;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import enums.Q_COUNT;
import enums.Q_TYPE;
import enums.RESPONSE_MDNS_TYPE;
import exceptions.NotValidDomainNameException;
import exceptions.NotValidIPException;
import javafx.scene.control.TreeItem;

public class Request {

	private String qName;
	private byte[] nameInBytes;
	private Q_COUNT qCount;
	private Q_TYPE qtype;
	private RESPONSE_MDNS_TYPE mdnsType;
	private static final int BYTE_SIZE_OF_QCLASS_AND_QTYPE = 4;
	private int size;
	private int endIndex;
	private TreeItem<String> root;
	private static final String KEY_NAME = "Name";
	private static final String KEY_QCOUNT = "Type";
	private static final String KEY_QTYPE = "Class";
	private static final String KEY_RESPONSE_TYPE = "Response type";
	private static final Logger LOGGER = Logger.getLogger(Language.class.getName());

	public Request(String qName, Q_COUNT qCount)
			throws NotValidIPException, UnsupportedEncodingException, NotValidDomainNameException {
		this.qName = qName;
		if (qCount.equals(Q_COUNT.PTR)) {
			ipAddressToPTRFormat();
		}
		this.nameInBytes = DomainConvert.encodeDNS(this.qName);
		this.qCount = qCount;
		this.qtype = Q_TYPE.IN;
		this.endIndex = 0;
		this.size = this.nameInBytes.length + BYTE_SIZE_OF_QCLASS_AND_QTYPE;
		this.mdnsType = null;
	}

	public Request(String qName, Q_COUNT a, RESPONSE_MDNS_TYPE mdnsType)
			throws NotValidIPException, UnsupportedEncodingException {
		this.qName = qName;
		this.mdnsType = mdnsType;
		if (a.equals(Q_COUNT.PTR)) {
			ipAddressToPTRFormat();
		}
		this.nameInBytes = DomainConvert.encodeMDNS(this.qName);
		this.qCount = a;
		this.qtype = Q_TYPE.IN;

		this.size = this.nameInBytes.length + BYTE_SIZE_OF_QCLASS_AND_QTYPE;
	}

	public Request() {
	};

	public byte[] getRequestAsBytes() {

		int lenghtOfName = nameInBytes.length;

		byte result[] = new byte[lenghtOfName + 4];
		for (int i = 0; i < lenghtOfName; i++) {
			result[i] = nameInBytes[i];
		}
		result[lenghtOfName] = qCount.code.getAsBytes()[1];
		result[lenghtOfName + 1] = qCount.code.getAsBytes()[0];

		if (mdnsType != null) {
			UInt16 newQtype = new UInt16(mdnsType.value + qtype.code.getValue());
			result[lenghtOfName + 2] = newQtype.getAsBytes()[1];
			result[lenghtOfName + 3] = newQtype.getAsBytes()[0];
			return result;
		}

		result[lenghtOfName + 2] = qtype.code.getAsBytes()[1];
		result[lenghtOfName + 3] = qtype.code.getAsBytes()[0];
		return result;
	}

	public TreeItem<String> getAsTreeItem() {
		root = new TreeItem<String>(qName + " " + qCount + " " + qtype);
		root.getChildren().add(new TreeItem<String>(KEY_NAME + ": " + qName));
		root.getChildren().add(new TreeItem<String>(KEY_QCOUNT + ": " + qCount));
		if (mdnsType != null) {
			root.getChildren().add(new TreeItem<String>(KEY_RESPONSE_TYPE + ": " + mdnsType.toString()));
		}
		root.getChildren().add(new TreeItem<String>(KEY_QTYPE + ": " + qtype));
		return root;
	}

	private void ipAddressToPTRFormat() throws NotValidIPException {
		if (qName.contains(".arpa")) {
			return;
		}
		this.qName = Ip.getIpReversed(qName);
	}

	@Override
	public String toString() {
		return "Request [qName=" + qName + ", nameInBytes=" + Arrays.toString(nameInBytes) + ", qCount=" + qCount
				+ ", qtype=" + qtype + ", mdnsType=" + mdnsType + ", size=" + size + ", endIndex=" + endIndex
				+ ", root=" + root + "]";
	}

	public int getSize() {
		return size;
	}

	public void printEncodedQnameInHex() {
		String res = "";
		for (byte b : nameInBytes) {
			res += String.format("%02x", b);
		}
		System.out.println(res);
	}

	public Request parseRequest(byte[] request, int startIndex) {

		int nameEndIndex = DomainConvert.getIndexOfLastByteOfName(request, startIndex);

		this.endIndex = nameEndIndex + BYTE_SIZE_OF_QCLASS_AND_QTYPE;

		byte[] encodedName = new byte[nameEndIndex - startIndex + 1];
		int j = 0;
		for (int i = startIndex; i < nameEndIndex + 1; i++) {
			encodedName[j] = request[i];
			j++;
		}
		this.nameInBytes = encodedName;
		this.qName = DomainConvert.decodeDNS(request, startIndex);
		this.qCount = Q_COUNT
				.getTypeByCode(new UInt16().loadFromBytes(request[this.endIndex - 3], request[this.endIndex - 2]));
		this.qtype = Q_TYPE
				.getTypeByCode(new UInt16().loadFromBytes(request[this.endIndex - 1], request[this.endIndex]));
		this.size = nameInBytes.length + BYTE_SIZE_OF_QCLASS_AND_QTYPE;
		LOGGER.info(this.toString());
		return this;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(KEY_NAME, qName);
		jsonObject.put(KEY_QCOUNT, qCount);
		if (mdnsType != null) {
			jsonObject.put(KEY_RESPONSE_TYPE, mdnsType.toString());
		}
		jsonObject.put(KEY_QTYPE, qtype);
		return jsonObject;
	}

}
