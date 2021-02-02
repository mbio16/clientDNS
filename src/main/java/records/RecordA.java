package records;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RecordA extends Record {

	private InetAddress ipv4Address;
	private String ipv4AddressAsString;
	public RecordA(byte[] rawMessage, int lenght, int startIndex) throws UnknownHostException {
		super(rawMessage, lenght, startIndex);
		parseRecord();
	}
	
	private  void parseRecord() throws UnknownHostException {
		byte data [] = new byte [lenght];
		int j = 0;
		for (int i = startIndex; i < startIndex+lenght; i++) {
			data[j] = rawMessage[i];
			j++;
		}
		ipv4Address = InetAddress.getByAddress(data);
		ipv4AddressAsString = ipv4Address.getHostAddress();
	}

	@Override
	public String toString() {
		return "RecordA [ipv4Address=" + ipv4Address + ", ipv4AddressAsString=" + ipv4AddressAsString + "]";
	}

	
}
