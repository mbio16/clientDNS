package allTests;

import enums.APPLICATION_PROTOCOL;
import enums.Qcount;
import enums.TRANSPORT_PROTOCOL;
import models.MessageParser;
import models.MessageSender;

public class main {

	public static void main(String[] args) {
		Qcount[] a = {Qcount.AAAA,Qcount.CAA};
		MessageSender sender;
		MessageParser parser;
		try {
			sender = new MessageSender(true, true, "seznam.cz",a ,TRANSPORT_PROTOCOL.TCP,APPLICATION_PROTOCOL.DNS,"8.8.8.8");
			sender.send();
			parser = new MessageParser(sender.getRecieveReply(),sender.getHeader());
			parser.parse();
			System.out.println(parser.getAsJson().toJSONString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}