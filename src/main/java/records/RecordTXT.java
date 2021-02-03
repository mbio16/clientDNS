package records;

import java.io.UnsupportedEncodingException;

public class RecordTXT extends Record{

	private String stringText;
	public RecordTXT(byte[] rawMessage, int lenght, int startIndex) throws UnsupportedEncodingException {
		super(rawMessage, lenght, startIndex);
		parse();
	}

	public void parse() throws UnsupportedEncodingException {
		byte [] textByte = new byte [lenght];
		int j=0;
		for (int i = startIndex+1; i < startIndex+lenght-1; i++) {
			textByte[j] = rawMessage[i];
			j++;
		}
		// haS TO BE REAPAIRED
			stringText = new String(textByte,"UTF-8");

	}
	
	@Override
	public String getDataAsString() {
		return stringText;
	}
	

}
