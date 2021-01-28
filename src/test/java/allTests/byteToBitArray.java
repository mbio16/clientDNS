package allTests;



import org.junit.jupiter.api.Test;

import models.Header;

class byteToBitArray {

	@Test
	void test() {
		Header h = new Header(false, true, 1);
		byte [] res = h.getHaderAsBytes();
		h.toString();
		for (byte b : res) {
			System.out.println(b);
		}
	}

}
