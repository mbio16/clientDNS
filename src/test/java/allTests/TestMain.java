package allTests;

import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import models.Ip;
import models.MessageParser;
import models.MessageSender;

public class TestMain {

	public static void main(String[] args) {
	//	Q_COUNT[] a = {Q_COUNT.A,Q_COUNT.AAAA};
	//	MessageSender sender;
	//	MessageParser parser;
		try {/*
			sender = new MessageSender(true, true,true,"seznam.cz",a ,TRANSPORT_PROTOCOL.UDP,APPLICATION_PROTOCOL.DNS,"193.17.47.1");
			sender.send();
			parser = new MessageParser(sender.getRecieveReply(),sender.getHeader());
			parser.parse();
			
			System.out.println(sender.getAsJsonString());
			System.out.println(parser.getAsJsonString());
			
			sender.getAsTreeItem();
			parser.getAsTreeItem();
			System.out.println(sender.getTimeElapsed());
			System.out.println(parser.getAsJson().toString());*/
			Ip ip = new Ip();
			System.out.println(ip.getIpv4DnsServer());
			System.out.println(ip.getIpv6DnsServer());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}