package models;



import java.util.Arrays;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import enums.Qcount;
import enums.Qtype;

public class Request {

	private String qName;
	private byte [] nameInBytes;
	private Qcount qCount;
	private Qtype qtype;
	private static final int BYTE_SIZE_OF_QCLASS_AND_QTYPE = 4;
	private int size;
	private int endIndex; 
	
	private static final String KEY_NAME="Name";
	private static final String KEY_QCOUNT="Type";
	private static final String KEY_QTYPE="Class";
	private static final Logger LOGGER = Logger.getLogger(Language.class.getName());
	public Request(String qName,Qcount qCount) throws Exception {
		this.qName = qName;
		this.nameInBytes = DomainConvert.encodeDNS(qName);
		this.qCount = qCount;
		this.qtype = Qtype.IN;
		this.endIndex = 0;
		this.size = this.nameInBytes.length+BYTE_SIZE_OF_QCLASS_AND_QTYPE;
	}
	
	public Request() {};
	
	public byte[] getRequestAsBytes() {
		
		int lenghtOfName = nameInBytes.length;
		
		byte result [] = new byte[lenghtOfName+4];
		for (int i = 0; i < lenghtOfName; i++) {
			result[i] = nameInBytes[i];
		}
		result[lenghtOfName] =qCount.code.getAsBytes()[1];
		result[lenghtOfName+1] =qCount.code.getAsBytes()[0];
		result[lenghtOfName+2] =qtype.code.getAsBytes()[1];
		result[lenghtOfName+3] = qtype.code.getAsBytes()[0];
		return result;
	}
	
	


	@Override
	public String toString() {
		return "Request [qName=" + qName + ", nameInBytes=" + Arrays.toString(nameInBytes) + ", qCount=" + qCount
				+ ", qtype=" + qtype + ", endIndex=" + endIndex + "]";
	}

	public int getSize() {
		return size;
	}

	public Request parseRequest(byte [] request,int startIndex) {
		
		
		int nameEndIndex = DomainConvert.getIndexOfLastByteOfName(request, startIndex);
		
		this.endIndex = nameEndIndex + BYTE_SIZE_OF_QCLASS_AND_QTYPE;

		
		byte [] encodedName = new byte[nameEndIndex-startIndex+1];
		int j=0;
		for (int i = startIndex; i < nameEndIndex+1; i++) {
			encodedName[j] = request[i];
			j++;
		}
		this.nameInBytes = encodedName;
		this.qName = DomainConvert.decodeDNS(encodedName);	
		this.qCount =  Qcount.getTypeByCode(new UInt16().loadFromBytes(request[this.endIndex-3],request[this.endIndex-2]));
		this.qtype = Qtype.getTypeByCode(new UInt16().loadFromBytes(request[this.endIndex-1],request[this.endIndex]));
		this.size = nameInBytes.length+BYTE_SIZE_OF_QCLASS_AND_QTYPE;
		LOGGER.info(this.toString());
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(KEY_NAME, qName);
		jsonObject.put(KEY_QCOUNT, qCount);
		jsonObject.put(KEY_QTYPE, qtype);
		return jsonObject;
	}
}
