package records;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import models.UInt16;

public class RecordOPT extends Record {
	
	private ArrayList<UInt16> optionCode;
	private ArrayList<UInt16> optionDataLenght;
	private ArrayList <String> optionData;
	
	private static final String KEY_OPTION_CODE="Option code";
	private static final String KEY_DATA_LENGHT="Data lenght";
	private static final String KEY_DATA="Data";
	private static final String KEY_RETURN="Option data";
	public RecordOPT(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		parse();
	}
	
	private void parse() {
		int currentIndex = startIndex;
		if (lenght>4) {
			while(rawMessage.length>currentIndex) {
			optionCode.add(new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]));
			currentIndex +=2;
			optionDataLenght.add(new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]));
			currentIndex +=2;
			String data = "";
			for (int i = currentIndex; i <currentIndex+optionDataLenght.get(optionData.size()-1).getValue(); i++) {
				data += (char) rawMessage[i];
				currentIndex++;
				}
			optionData.add(data);
			}
		}
		else{
			optionCode=null;
			optionData=null;
			optionDataLenght=null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJson() {
		
		JSONArray jsonArray = new JSONArray();
		if(optionCode.equals(null)) {
			return new JSONObject();
		}
		JSONObject jsonSupObject = new JSONObject();	
		for (int i = 0; i < optionCode.size(); i++) {
			jsonSupObject.put(KEY_OPTION_CODE, optionCode.get(i));
			jsonSupObject.put(KEY_DATA_LENGHT,optionDataLenght.get(i));
			jsonSupObject.put(KEY_DATA,optionData.get(i));
			jsonArray.add(jsonSupObject);
		}
		JSONObject returnObject = new JSONObject();
		returnObject.put(KEY_RETURN,jsonArray);
		return returnObject;
	}
	

}
