package allTests;
import java.net.NetworkInterface;
import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import models.MessageParser;
import models.MessageSender;
public class TestMain {

	public static void main(String[] args) {
		Q_COUNT[] a = {Q_COUNT.A};
		MessageSender sender;
		MessageParser parser;
		TRANSPORT_PROTOCOL protocol = TRANSPORT_PROTOCOL.UDP;
		try {
		
	//	sender = new MessageSender(false,"macMartin.local",a,IP_PROTOCOL.IPv4,RESPONSE_MDNS_TYPE.RESPONSE_MULTICAST);
		sender = new MessageSender(true, false, false, ".", a,protocol,APPLICATION_PROTOCOL.DNS,"1.1.1.1");
		sender.setInterfaceToSend(NetworkInterface.getByName("eth7"));
		sender.send();
		
		sender.getAsJsonString();
		parser = new MessageParser(sender.getRecieveReply(), sender.getHeader(), protocol);
		//parser.parseMDNS();
		parser.parse();
		System.out.println(parser.getAsJsonString());
//			https://dns.google/resolve?
		//sender = new MessageSender(true, false, false, "seznam.cz", a, protocol,APPLICATION_PROTOCOL.DOH,"https://cloudflare-dns.com/dns-query");
//		sender = new MessageSender(true, false, true, "seznam.cz", a, protocol,APPLICATION_PROTOCOL.DOH,"1.1.1.1");
//		sender.send();
//		parser = new MessageParser(sender.getHttpResponse());
//		System.out.println(sender.getDoHRequest());
//		System.out.println(parser.getAsJsonString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}