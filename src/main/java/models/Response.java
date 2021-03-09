package models;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import enums.Q_COUNT;
import enums.Q_TYPE;
import javafx.scene.control.TreeItem;
import records.Record;
import records.RecordA;
import records.RecordAAAA;
import records.RecordCAA;
import records.RecordCNAME;
import records.RecordDNSKEY;
import records.RecordDS;
import records.RecordMX;
import records.RecordNS;
import records.RecordNSEC;
import records.RecordNSEC3;
import records.RecordOPT;
import records.RecordPTR;
import records.RecordRRSIG;
import records.RecordSOA;
import records.RecordTXT;
public class Response {

	private byte [] rawMessage;
	private String nameAsString;
	private Q_COUNT qcount;;
	private Q_TYPE qtype;
	private int ttl;
	private UInt16 rdLenght;
	private int byteSize;
	private int endIndex;
	private Record rdata;
	private byte rCode;
	private byte version;
	private UInt16 doBit;
	private UInt16 size;
	private static final int  COMPRESS_CONTANT_NUMBER=49152;
	private static final int DO_BIT_VALUE=32768;
	private static final String DATA_KEY="Data";
	private static final String NAME_KEY="Name";
	private static final String TYPE_KEY="Type";
	private static final String TTL_KEY="Time to Live";
	private static final String CLASS_KEY="Class";
	
	private static final String KEY_OPT_UDP_SIZE="Size";
	private static final String KEY_OPT_RCODE="Rcode";
	private static final String KEY_OPT_VERSION="EDSN0 version";
	private static final String KEY_OPT_DO_BIT = "Can handle DNSSEC";
	public static final String ROOT_DOMAIN = ". (ROOT)";
	public Response() {
		
	}
	
	public Response parseResponse(byte [] rawMessage,int startIndex) throws UnknownHostException, UnsupportedEncodingException {
		this.rawMessage = rawMessage;
		int currentIndex = startIndex;
		currentIndex = parseName(currentIndex);
		this.qcount = Q_COUNT.getTypeByCode(new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]));
		currentIndex += 2;
		if (qcount.equals(Q_COUNT.OPT)) {
		parseAsOPT(currentIndex);
		return this;
		}
		this.qtype = Q_TYPE.getTypeByCode(new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]));
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
	
	private void parseAsOPT(int currentIndex) throws UnknownHostException, UnsupportedEncodingException {
		size = new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]);
		currentIndex +=2;
		rCode = rawMessage[currentIndex];
		currentIndex += 1;
		version = rawMessage[currentIndex];
		currentIndex += 1;
		doBit = new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]);
		currentIndex +=2;
		rdLenght = new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]);
		currentIndex += 2;
		this.rdata = parseRecord(currentIndex);
		nameAsString = ". (ROOT)";
		this.endIndex = currentIndex;
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
		case DNSKEY:
			return new RecordDNSKEY(rawMessage,rdLenght.getValue(), currentIndex);
		case CAA:
			return new RecordCAA(rawMessage, rdLenght.getValue(),currentIndex);
		case RRSIG:
			return new RecordRRSIG(rawMessage, rdLenght.getValue(),currentIndex);
		case OPT:
			return new RecordOPT(rawMessage, rdLenght.getValue(),currentIndex);
		case PTR:
			return new RecordPTR(rawMessage,rdLenght.getValue(),currentIndex);
		case DS:
			return new RecordDS(rawMessage,rdLenght.getValue(),currentIndex);
		case NSEC:
			return new RecordNSEC(rawMessage,rdLenght.getValue(),currentIndex);
		case NSEC3:
			return new RecordNSEC3(rawMessage, rdLenght.getValue(),currentIndex);
		default:
			return null;
		}
	}

	
	public TreeItem<String> getAsTreeItem(){		
		TreeItem<String>  main = new TreeItem<String>(nameAsString + " " + qcount + " " + rdata.getDataForTreeViewName());
		main.getChildren().add(new TreeItem<String>(NAME_KEY + ": " +nameAsString));
		main.getChildren().add(new TreeItem<String>(TYPE_KEY + ": " + qcount));
		if(qcount.code != Q_COUNT.OPT.code) {
		main.getChildren().add(new TreeItem<String>(TTL_KEY + ": " + ttl));
		main.getChildren().add(new TreeItem<String>(CLASS_KEY + ": " + qtype));
		for (String item : rdata.getValesForTreeItem()) {
			main.getChildren().add(new TreeItem<String>(item));
			}
		}
		else {
		main.setValue(nameAsString + " " + qcount);

		main.getChildren().add(new TreeItem<String>(KEY_OPT_RCODE + ": "+ (int) rCode));
		main.getChildren().add(new TreeItem<String>(KEY_OPT_VERSION + ": " + (int)version));
		main.getChildren().add(new TreeItem<String>(KEY_OPT_UDP_SIZE + ": " + size.getValue()));
		String doBitString = doBit.getValue() >= DO_BIT_VALUE ? "true":"false";
		main.getChildren().add(new TreeItem<String>(KEY_OPT_DO_BIT + ": "+  doBitString));
		}
		
		return main;
	}
	
	public static TreeItem<String> getOptAsTreeItem() {
		TreeItem<String> root= new  TreeItem<String>(ROOT_DOMAIN + " " + Q_COUNT.OPT);
		root.getChildren().add(new TreeItem<String>(NAME_KEY + ": " + ROOT_DOMAIN ));
		root.getChildren().add(new TreeItem<String>(TYPE_KEY + ": " + Q_COUNT.OPT));
		root.getChildren().add(new TreeItem<String>(KEY_OPT_RCODE + ": "+ 0));
		root.getChildren().add(new TreeItem<String>(KEY_OPT_VERSION + ": " + 0));
		root.getChildren().add(new TreeItem<String>(KEY_OPT_UDP_SIZE + ": " + 1232));
		root.getChildren().add(new TreeItem<String>(KEY_OPT_DO_BIT + ": " +  "true"));
		return root;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		if (qcount.equals(Q_COUNT.OPT)) {
			return getOPTAsJson();
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(NAME_KEY,nameAsString);
		jsonObject.put(TYPE_KEY,qcount.toString());
		jsonObject.put(CLASS_KEY, qtype);	
		jsonObject.put(TTL_KEY,ttl);
		jsonObject.put(DATA_KEY,rdata.getAsJson());
		return jsonObject;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getOPTAsJson() {
		JSONObject json = new JSONObject();
		json.put(KEY_OPT_UDP_SIZE,size.getValue());
		json.put(KEY_OPT_RCODE,(int) rCode);
		json.put(KEY_OPT_VERSION, (int)version);
		json.put(KEY_OPT_DO_BIT,doBit.getValue() >= DO_BIT_VALUE ? true:false);
		return json;
	}
	
	
	public byte [] getDnssecAsBytes() {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		bytes.add((byte) 0x00);
		bytes.add(Q_COUNT.OPT.code.getAsBytes()[1]);
		bytes.add(Q_COUNT.OPT.code.getAsBytes()[0]);
		bytes.add((byte) new UInt16(MessageSender.MAX_UDP_SIZE).getAsBytes()[1]);
		bytes.add((byte) new UInt16(MessageSender.MAX_UDP_SIZE).getAsBytes()[0]);
		bytes.add((byte) 0x00);
		bytes.add((byte) 0x00);
		bytes.add((byte) new UInt16(DO_BIT_VALUE).getAsBytes()[1]);
		bytes.add((byte) new UInt16(DO_BIT_VALUE).getAsBytes()[0]);
		bytes.add((byte) 0x00);
		bytes.add((byte) 0x00);
		
		byte [] returnArray = new byte [bytes.size()];
		
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = bytes.get(i);
		}
		return returnArray;
	}
	public byte[] getRawMessage() {
		return rawMessage;
	}

	public String getNameAsString() {
		return nameAsString;
	}

	public Q_COUNT getQcount() {
		return qcount;
	}

	public Q_TYPE getQtype() {
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
