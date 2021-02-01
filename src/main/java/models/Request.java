package models;

import java.nio.charset.StandardCharsets;

import enums.Qcount;
import enums.Qtype;

public class Request {

	private String qName;
	private Qcount qCount;
	private Qtype qtype;
	private static final int BYTE_SIZE_OF_QCLASS_AND_QTYPE = 4;
	public Request(String qName,Qcount qCount) throws Exception {
		this.qName = DomainConvert.encodeDNS(qName);
		this.qCount = qCount;
		this.qtype = Qtype.IN;
	}
	
	public Request() {};
	public byte[] getRequestAsBytes() {
		byte [] nameInBytes = qName.getBytes(StandardCharsets.US_ASCII);
		
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
		return "Request [qName=" + qName + ", qCount=" + qCount + ", qtype=" + qtype + "]";
	}

	public Request parseRequest(byte [] request) {
		int size = request.length - BYTE_SIZE_OF_QCLASS_AND_QTYPE;
		byte [] encodedName = new byte[size];
		for (int i = 0; i < size; i++) {
			encodedName[i]=request[i];
		}
		String name= new String(encodedName);
		this.qName = DomainConvert.decodeDNS(name);
		System.out.println(qName);
		
		this.qCount =  Qcount.getTypeByCode(new UInt16().loadFromBytes(request[size],request[size+1]));
		this.qtype = Qtype.getTypeByCode(new UInt16().loadFromBytes(request[size+2],request[size+3]));
		return this;
	}
}
