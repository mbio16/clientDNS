package allTests;

import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import models.MessageParser;
import models.MessageSender;

public class TestMain {

	public static void main(String[] args) {
		Q_COUNT[] a = {Q_COUNT.NSEC3};
		MessageSender sender;
		MessageParser parser;
		TRANSPORT_PROTOCOL protocol = TRANSPORT_PROTOCOL.TCP;
		try {
			sender = new MessageSender(true, true,true,"biolek.net.",a ,protocol,APPLICATION_PROTOCOL.DNS,"193.17.47.1");
			sender.send();
			parser = new MessageParser(sender.getRecieveReply(),sender.getHeader(),protocol);
			parser.parse();
			
			System.out.println(sender.getAsJsonString());
			System.out.println(parser.getAsJsonString());
			System.out.println("Message size query: " + sender.getByteSizeQuery());
			System.out.println("Messge size response: " + parser.getByteSizeResponse());
			sender.getAsTreeItem();
			parser.getAsTreeItem();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}