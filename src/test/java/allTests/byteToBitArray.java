package allTests;



import org.junit.jupiter.api.Test;

import models.Header;

class byteToBitArray {

	@Test
	void test() {
		Header h = new Header(true,true, 1,true);
		byte [] res = h.getHaderAsBytes();
		h.toString();
		for (byte b : res) {
			System.out.println(b);
		}
	}

}
