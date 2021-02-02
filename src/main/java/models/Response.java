package models;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import enums.Qcount;
import enums.Qtype;
import records.Record;
import records.RecordA;
import records.RecordAAAA;

public class Response {

	private byte [] rawMessage;
	private String nameAsString;
	private Qcount qcount;;
	private Qtype qtype;
	private int ttl;
	private UInt16 rdLenght;
	private int byteSize;
	private int endIndex;
	private Record rdata;
	private static final int  COMPRESS_CONTANT_NUMBER=49152;
	public Response() {
		
	}
	
	public Response parseResponse(byte [] rawMessage,int startIndex) throws UnknownHostException {
		this.rawMessage = rawMessage;
		int currentIndex = startIndex;
		currentIndex = parseName(currentIndex);
		this.qcount = Qcount.getTypeByCode(new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]));
		currentIndex += 2;
		this.qtype = Qtype.getTypeByCode(new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]));
		currentIndex += 2;
		byte [] ttlBytes = {
				rawMessage[currentIndex],
				rawMessage[currentIndex+1],
				rawMessage[currentIndex+2],
				rawMessage[currentIndex+3]
		};
		this.ttl = ByteBuffer.wrap(ttlBytes).getInt();	
		currentIndex +=4;
		this.rdLenght = new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]);
		currentIndex +=2;
		this.endIndex = currentIndex + this.rdLenght.getValue()-1;
		this.rdata = parseRecord(currentIndex);
		return this;
	}
	private int parseName(int startIndex) {
		int positionOfNameIndex=startIndex;
		UInt16 firstTwoBytes = new UInt16().loadFromBytes(rawMessage[startIndex],rawMessage[startIndex+1]);
		if (firstTwoBytes.getValue()>=COMPRESS_CONTANT_NUMBER) {
			//compress Form
			UInt16 nameStartByte = new UInt16(firstTwoBytes.getValue()-COMPRESS_CONTANT_NUMBER);
			positionOfNameIndex = nameStartByte.getValue();
			startIndex +=2;
		}
		else {
			startIndex  = DomainConvert.getIndexOfLastByteOfName(rawMessage, startIndex)+1;
		}
		this.nameAsString = DomainConvert.decodeDNS(rawMessage, positionOfNameIndex);
		return startIndex;
	}
	
	private Record parseRecord(int currentIndex) throws UnknownHostException {
		switch (qcount) {
		case A:
			return new RecordA(rawMessage, rdLenght.getValue(),currentIndex);
		case AAAA:
			return new RecordAAAA(rawMessage,  rdLenght.getValue(),currentIndex);
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return "Response [nameAsString=" + nameAsString + ", qcount="
				+ qcount + ", qtype=" + qtype + ", ttl=" + ttl + ", rdLenght=" + rdLenght + ", byteSize=" + byteSize
				+ ", endIndex=" + endIndex + ", rdata=" + rdata + "]";
	}
	
	public byte[] getRawMessage() {
		return rawMessage;
	}

	public String getNameAsString() {
		return nameAsString;
	}

	public Qcount getQcount() {
		return qcount;
	}

	public Qtype getQtype() {
		return qtype;
	}

	public int getTtl() {
		return ttl;
	}

	public UInt16 getRdLenght() {
		return rdLenght;
	}

	public int getByteSize() {
		return byteSize;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public Record getRdata() {
		return rdata;
	}



	
	
	
	
	
}
