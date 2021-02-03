package records;


import models.DomainConvert;

public class RecordCNAME extends Record{

	private String name;
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
	
}
