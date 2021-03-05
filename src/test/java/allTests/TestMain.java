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
		try {
			sender = new MessageSender(true, true,true,"biolek.net.",a ,TRANSPORT_PROTOCOL.UDP,APPLICATION_PROTOCOL.DNS,"193.17.47.1");
			sender.send();
			parser = new MessageParser(sender.getRecieveReply(),sender.getHeader());
			parser.parse();
			
			System.out.println(sender.getAsJsonString());
			System.out.println(parser.getAsJsonString());
			
			sender.getAsTreeItem();
			parser.getAsTreeItem();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}