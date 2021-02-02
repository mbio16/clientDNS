package models;

import enums.Qcount;
import enums.Qtype;

public class Response {

	private byte [] rawMessage;
	private byte [] name;
	private String nameAsString;
	private Qcount qcount;;
	private Qtype qtype;
	private UInt16 ttl;
	private UInt16 rdLenght;
	private int byteSize;
	private int endIndex;
	public Response(byte [] rawMessage) {
		this.rawMessage = rawMessage;
	}
	
	public void parseResponse(int startIndex) {
		int currentIndex = startIndex;
		byte firstByte = rawMessage[currentIndex];
		boolean [] firstByteAsArray = DataTypesConverter.byteToBoolArr(firstByte, 8);
		if (firstByteAsArray[6] && firstByteAsArray[7]) {
			//comprese
			
		}
		
		
	}
	
	
	
}
