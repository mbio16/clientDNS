package records;

import org.json.simple.JSONObject;

public class Record {
	protected int lenght;
	protected int startIndex;
	protected byte rawMessage[];
	
	
	public Record(byte [] rawMessage,int lenght,int startIndex) {
		this.lenght = lenght;
		this.rawMessage = rawMessage;
		this.startIndex = startIndex;
	}

	public JSONObject getAsJson() {
		return new JSONObject();
	}
	
	public int getLenght() {
		return lenght;
	}


	public int getStartIndex() {
		return startIndex;
	}


	public byte[] getRawMessage() {
		return rawMessage;
	}
	
	//Has to be overided in the children
	public String getDataAsString() {
		return null;
	}
	
	
	
}
