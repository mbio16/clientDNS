package models;

import enums.Qcount;
import enums.Qtype;

public class Response {

	private byte [] wholeMessage;
	private byte [] name;
	private byte [] response;
	private String nameAsString;
	private Qcount qcount;
	private Qtype qtype;
	private UInt16 ttl;
	private UInt16 rdLenght;
	private int byteSize;
	private int endIndex;
	public Response(byte [] wholeMessage, byte [] response) {
		this.wholeMessage = wholeMessage;
		this.response = response;
	}
	
//	private void parseResponse() {
//		res
//	}
	
	
	
}
