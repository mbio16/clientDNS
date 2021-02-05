package allTests;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import models.DomainConvert;

class Domain {
	byte [] resultTestDomain = {
			(byte) 0x04,
			(byte) 0x74,
			(byte) 0x65,
			(byte) 0x73,
			(byte) 0x74,
			(byte) 0x06,
			(byte) 0x64,
			(byte) 0x6f,
			(byte) 0x6d,
			(byte) 0x61,
			(byte) 0x69,
			(byte) 0x6e,
			(byte) 0x02,
			(byte) 0x63,
			(byte) 0x7a,
			(byte) 0x00
	};
	
	byte [] resultHackyCarky = {
			(byte) 0x13,
			(byte) 0x78,
			(byte) 0x6e,
			(byte) 0x2d,
			(byte) 0x2d,
			(byte) 0x68,
			(byte) 0x6b,
			(byte) 0x79,
			(byte) 0x72,
			(byte) 0x6b,
			(byte) 0x79,
			(byte) 0x2d,
			(byte) 0x70,
			(byte) 0x74,
			(byte) 0x61,
			(byte) 0x63,
			(byte) 0x37,
			(byte) 0x30,
			(byte) 0x62,
			(byte) 0x63,
			(byte) 0x02,
			(byte) 0x63,
			(byte) 0x7a,
			(byte) 0x00};

	@Test
	void asciiDomain() {
		try {
			
			
			byte output[] = DomainConvert.encodeDNS("test.domain.cz");
			assertEquals(Arrays.toString(resultTestDomain),Arrays.toString(output));
		}
	 catch (Exception e) {
		 e.printStackTrace();
		}
	}	
	
	@Test
	void utfDomain() {
		try {
			
			
		assertEquals(Arrays.toString(resultHackyCarky),Arrays.toString(DomainConvert.encodeDNS("h·ËkyË·rky.cz")));
		}
		catch (Exception e) {
			System.out.println("Not valid domain");
		}
		}

	@Test
	void asciDomainDecode() {
		assertEquals("test.domain.cz",DomainConvert.decodeDNS(resultTestDomain));
	}
	@Test
	void utfDomainDecode() {
		assertEquals("xn--hkyrky-ptac70bc.cz",DomainConvert.decodeDNS(resultHackyCarky));
	}
}
