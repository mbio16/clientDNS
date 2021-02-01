package allTests;

import enums.APPLICATION_PROTOCOL;
import enums.Qcount;
import enums.TRANSPORT_PROTOCOL;

import models.MessageSender;

public class main {

	public static void main(String[] args) {
		Qcount[] a = {Qcount.A,Qcount.AAAA};
		MessageSender sender = new MessageSender(true, true, "seznam.cz",a ,TRANSPORT_PROTOCOL.TCP,APPLICATION_PROTOCOL.DNS,"1.1.1.1");
		try {
			sender.send();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}