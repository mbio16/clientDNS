package records;


import org.json.simple.JSONObject;

import models.DomainConvert;

public class RecordCNAME extends Record{

	protected String name;
	private static String KEY_CNAME="Name";
	public RecordCNAME(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		parseRecord();
	}

	
	
	private  void parseRecord() {
		name = 	DomainConvert.decodeDNS(rawMessage, startIndex);
	}

	
	
	
	@Override
	public String toString() {
		return "RecordCname [name=" + name + "]";
	}



	@Override
	public String getDataAsString() {		
		return name;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_CNAME, name);
		return object;
	}
	
}
