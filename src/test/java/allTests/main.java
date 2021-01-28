package allTests;

import enums.Qcount;
import models.Header;
import models.Request;

public class main {

	public static void main(String[] args) {
		/*Header h = new Header(true, true, 106);
		byte [] res = h.getHaderAsBytes();
		
		for (byte b : res) {
			System.out.println(String.format("%02x", b));
		}
		
		Header k = h.parseHead(res);
		System.out.println(h.toString());
		System.out.println(k.toString());
		
		System.out.println(String.format("%02x",((byte) 28)));*/
		
		try {
		Request r = new Request("www.seznam.cz", Qcount.A);
		byte [] result= r.getRequestAsBytes();
		for (byte b : result) {
			System.out.println(String.format("%02x", b));
		}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	

}