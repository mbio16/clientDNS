package models;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.json.simple.JSONObject;
import enums.Qcount;
import enums.Qtype;
import records.Record;
import records.RecordA;
import records.RecordAAAA;
import records.RecordCNAME;
import records.RecordMX;
import records.RecordNS;
import records.RecordSOA;
import records.RecordTXT;

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
	private static final String DATA_KEY="Data";
	private static final String NAME_KEY="Name";
	private static final String TYPE_KEY="Type";
	private static final String TTL_KEY="Time to Live";
	private static final String CLASS_KEY="Class";
	public Response() {
		
	}
	
	public Response parseResponse(byte [] rawMessage,int startIndex) throws UnknownHostException, UnsupportedEncodingException {
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
		//Has to be done seperately (not in DomainConvert), because of end index
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
	
	private Record parseRecord(int currentIndex) throws UnknownHostException, UnsupportedEncodingException {
		switch (qcount) {
		case A:
			return new RecordA(rawMessage, rdLenght.getValue(),currentIndex);
		case AAAA:
			return new RecordAAAA(rawMessage,  rdLenght.getValue(),currentIndex);
		case CNAME:
			return new RecordCNAME(rawMessage, rdLenght.getValue(),currentIndex);
		case NS:
			return new RecordNS(rawMessage, rdLenght.getValue(),currentIndex);
		case TXT:
			return new RecordTXT(rawMessage, rdLenght.getValue(),currentIndex);
		case MX:
			return new RecordMX(rawMessage, rdLenght.getValue(),currentIndex);
		case SOA:
			return new RecordSOA(rawMessage, rdLenght.getValue(),currentIndex);
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
	

	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(NAME_KEY,nameAsString);
		jsonObject.put(TYPE_KEY,qcount.toString());
		jsonObject.put(CLASS_KEY, qtype);	
		jsonObject.put(TTL_KEY,ttl);
		jsonObject.put(DATA_KEY,rdata.getAsJson());
		return jsonObject;
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
