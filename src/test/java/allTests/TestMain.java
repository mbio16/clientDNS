package allTests;
import java.util.concurrent.TimeUnit;
import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import models.MessageParser;
import models.MessageSender;
import models.TCPConnection;

public class TestMain {

	public static void main(String[] args) {
		Q_COUNT[] a = {Q_COUNT.NSEC3PARAM};
		MessageSender sender;
		MessageParser parser;
		TRANSPORT_PROTOCOL protocol = TRANSPORT_PROTOCOL.UDP;
		try {
		/*	sender = new MessageSender(true, true,true,"biolek.net.",a ,protocol,APPLICATION_PROTOCOL.DNS,"8.8.8.8");
			sender.setCloseConnection(false);
			sender.send();
			TCPConnection t = sender.getTcp();
			parser = new MessageParser(sender.getRecieveReply(),sender.getHeader(),protocol);
			parser.parse();
			
			System.out.println(sender.getAsJsonString());
			System.out.println(parser.getAsJsonString());
			System.out.println("Message size query: " + sender.getByteSizeQuery());
			System.out.println("Messge size response: " + parser.getByteSizeResponse());
			sender.getAsTreeItem();
			parser.getAsTreeItem();
			
			TimeUnit.SECONDS.sleep(5);
			sender = new MessageSender(true, true,true,"seznam.net.",a ,protocol,APPLICATION_PROTOCOL.DNS,"8.8.8.8");
			sender.setTcp(t);
			sender.setCloseConnection(true);
			sender.send();
			parser = new MessageParser(sender.getRecieveReply(),sender.getHeader(),protocol);
			parser.parse();
			
			System.out.println(sender.getAsJsonString());
			System.out.println(parser.getAsJsonString());
			System.out.println("Message size query: " + sender.getByteSizeQuery());
			System.out.println("Messge size response: " + parser.getByteSizeResponse());*/
			
			sender = new MessageSender(true, true,true,"biolek.net.",a ,protocol,APPLICATION_PROTOCOL.DNS,"8.8.8.8");
			sender.send();
			parser = new MessageParser(sender.getRecieveReply(),sender.getHeader(),protocol);
			parser.parse();
			System.out.println(sender.getAsJsonString());
			System.out.println(parser.getAsJsonString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}