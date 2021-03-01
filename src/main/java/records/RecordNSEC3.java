package records;

import org.json.simple.JSONObject;
import enums.DIGEST_TYPE;
import models.UInt16;

public class RecordNSEC3 extends  Record {

	private DIGEST_TYPE hash;
	private byte flags;
	private UInt16 iteration;
	private int saltLenght;
	private String salt;
	private int hashLenght;
	private String name;
	
	private static final String KEY_HASH_TYPE="HASH_TYPE";
	private static final String KEY_FLAGS="FLAGS";
	private static final String KEY_ITERATION="ITERATIONS";
	private static final String KEY_SALT="SALT";
	private static final String KEY_SALT_LENGHT = "SALT_LENGHT";
	private static final String KEY_HASH_LENGHT="HASH_LENGHT";
	private static final String KEY_NEXT_OWNER_HASH="NEXT_OWNER_HASH";
	 public RecordNSEC3(byte[] rawMessage, int lenght, int startIndex){
		super(rawMessage, lenght, startIndex);
		 salt = "";
		 name = "";
		parseRecord();
	}
	
	private  void parseRecord(){
		hash = DIGEST_TYPE.getTypeByCode(rawMessage[startIndex]);
		flags = rawMessage[startIndex+1];
		int currentIndex = startIndex + 2;
		
		iteration = new UInt16().loadFromBytes(rawMessage[currentIndex],rawMessage[currentIndex+1]);
		currentIndex += 2;
		saltLenght = (int) rawMessage[currentIndex];
		currentIndex += 1;
		for (int i = currentIndex; i < currentIndex+saltLenght; i++) {
			salt += String.format("%02x", rawMessage[i]);
		}
		
		currentIndex += saltLenght;
		hashLenght = (int) rawMessage[currentIndex];
		currentIndex += 1;
		for (int i = currentIndex; i < currentIndex+hashLenght; i++) {
			name += String.format("%02x", rawMessage[i]);
		}
	}

	@Override
	public String toString() {
		return "";
	}
	
	@Override
	public String getDataAsString() {
		
		return "";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		 object.put(KEY_HASH_TYPE,hash);
		 object.put(KEY_FLAGS,(int) flags);
		 object.put(KEY_ITERATION,iteration.getValue());
		 object.put(KEY_SALT,salt);
		 object.put(KEY_SALT_LENGHT,saltLenght);
		 object.put(KEY_HASH_LENGHT,hashLenght);
		object.put(KEY_NEXT_OWNER_HASH,name);
		return object;
	}


	@Override
	public String [] getValesForTreeItem(){
		String [] pole = {
				
				 KEY_HASH_TYPE +": " + hash,
				 KEY_FLAGS +": "+ flags,
				 KEY_ITERATION + ": " +iteration.getValue(),
				 KEY_SALT + ": " + salt,
				 KEY_SALT_LENGHT +": "+saltLenght,
				 KEY_HASH_LENGHT + ": "+hashLenght,
				KEY_NEXT_OWNER_HASH +": " + name
		};
		return pole;
	}
	@Override
	public String getDataForTreeViewName() {
		return hash +" " + flags +" " + iteration + " ...";
	}
}
