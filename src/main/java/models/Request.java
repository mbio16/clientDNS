package models;

import java.nio.charset.StandardCharsets;

import enums.Qcount;

public class Request {

	private String qName;
	private Qcount qCount;
	private Qtype qtype;
	
	public Request(String qName,Qcount qCount) throws Exception {
		this.qName = DomainConvert.encodeDNS(qName);
		this.qCount = qCount;
		this.qtype = Qtype.IN;
	}
	
	public byte[] getRequestAsBytes() {
		UInt16 QcountAsUint = new UInt16((int) qCount.code);
		UInt16 QtypeAsUiInt16 = new UInt16((int) qtype.code);
		byte [] nameInBytes = qName.getBytes(StandardCharsets.US_ASCII);
		
		int lenghtOfName = nameInBytes.length;
		
		byte result [] = new byte[lenghtOfName+4];
		for (int i = 0; i < lenghtOfName; i++) {
			result[i] = nameInBytes[i];
		}
		result[lenghtOfName] = QcountAsUint.getAsBytes()[1];
		result[lenghtOfName+1] = QcountAsUint.getAsBytes()[0];
		result[lenghtOfName+2] = QtypeAsUiInt16.getAsBytes()[1];
		result[lenghtOfName+3] = QtypeAsUiInt16.getAsBytes()[0];
		return result;
	}
	
	public void parseRequest(byte [] request) {
		int size = request.length - 4;
		byte [] encodedName = new byte[size];
		for (int i = 0; i < size; i++) {
			encodedName[i]=request[i];
		}
		String name= new String(encodedName);
		qName = DomainConvert.decodeDNS(name);
		// TO DO 
	}
}
