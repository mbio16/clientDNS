package allTests;
import java.util.concurrent.TimeUnit;
import enums.APPLICATION_PROTOCOL;
import enums.IP_PROTOCOL;
import enums.Q_COUNT;
import enums.RESPONSE_MDNS_TYPE;
import enums.TRANSPORT_PROTOCOL;
import models.DomainConvert;
import models.Header;
import models.MessageParser;
import models.MessageSender;
import models.Request;
import models.TCPConnection;

public class TestMain {

	public static void main(String[] args) {
		Q_COUNT[] a = {Q_COUNT.A};
		MessageSender sender;
		MessageParser parser;
		TRANSPORT_PROTOCOL protocol = TRANSPORT_PROTOCOL.UDP;
		try {
		
//		sender = new MessageSender(false,"Samsung-Galaxy-S20.local",a,IP_PROTOCOL.IPv4,RESPONSE_MDNS_TYPE.RESPONSE_MULTICAST);
//		sender.send();
//		sender.getAsJsonString();
//		parser = new MessageParser(sender.getRecieveReply(), sender.getHeader(), null);
//		parser.parseMDNS();
//		System.out.println(parser.getAsJsonString());
			
		sender = new MessageSender(true, false, false, "seznam.cz", a, protocol,APPLICATION_PROTOCOL.DOH,null);
		sender.send();
		parser = new MessageParser(sender.getRecieveReply(), sender.getHeader(),TRANSPORT_PROTOCOL.UDP);
		parser.parse();
		System.out.println(parser.getAsJsonString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}