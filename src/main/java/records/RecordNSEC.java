package records;


public class RecordNSEC extends RecordCNAME{
	public RecordNSEC(byte[] rawMessage, int lenght, int startIndex) {
		super(rawMessage, lenght, startIndex);
		KEY_CNAME="NEXT_DOMAIN_NAME";
	}
}
