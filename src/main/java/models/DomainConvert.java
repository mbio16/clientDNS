package models;

import java.nio.charset.Charset;
import java.util.ArrayList;
//import java.util.logging.Logger;
import java.util.regex.Pattern;

public class DomainConvert {
	private static Pattern pDomainNameOnly;
    private static final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
    private static final int  COMPRESS_CONTANT_NUMBER=49152;
    private static final byte [] ROOT = {(byte) 0x00}; 
    static {
        pDomainNameOnly = Pattern.compile(DOMAIN_NAME_PATTERN);
    }
	
	//private static Logger LOGGER = Logger.getLogger(DomainConvert.class.getName());
	
	public static byte [] encodeDNS(String domain) throws Exception {
		if (domain.equals(null) || domain.equals("")) {
			 return ROOT;
		}
		if (!Charset.forName("US-ASCII").newEncoder().canEncode(domain)) {
			domain = Punycode.toPunycode(domain);
		}
		if(!isValidDomainName(domain)) {
			throw new Exception("Domain not valid");
		}
		ArrayList<Byte> resultByte = new ArrayList<Byte>();
		String splited [] = domain.split("\\.");
		for (String string : splited) {			
			resultByte.add((byte) string.length());
			 byte [] toAdd = string.getBytes("Us-ASCII");
			for (int i = 0; i < toAdd.length; i++) {
				resultByte.add(toAdd[i]);
			}
		}	
		resultByte.add((byte) 0x00);
		byte [] arrayToReturn = new byte [resultByte.size()];
		
		for (int i = 0; i < arrayToReturn.length; i++) {
			arrayToReturn[i] = resultByte.get(i);
		}
		return arrayToReturn;
	}
	
	public static String decodeDNS(byte [] encodedDomain) {
		int passed = 0;
		String result = "";
		while(true) {
		int size = (int) encodedDomain[passed];
		if (size == 0) 
		{
			return result.substring(0,result.length()-1);
		}
		else {
		for (int i = passed+1; i < passed+size+1; i++) {
			result +=(char)encodedDomain[i];
		}
		passed += size+1;
		result += ".";
		}
		}
	}
	
	public static String decodeDNS(byte [] encodedDomain, int startIndex) {
		int passed = startIndex;
		String result = "";
		while(true) {
		int size = (int) encodedDomain[passed];
		if (size == 0) 
		{
			return result.substring(0,result.length()-1);
		}
		else {
			if(size !=1) {
				if(isDnsNameCompressed(encodedDomain,passed)) {
					return result+getCompressedName(encodedDomain, passed);
				}
			}
			
			for (int i = passed+1; i < passed+size+1; i++) {
				result +=(char)encodedDomain[i];
			}
			passed += size+1;
			result += ".";
		}
		}
	}
	
	
	private static boolean isDnsNameCompressed(byte rawMessage [],int currentPosition) {
		UInt16 firstTwoBytes = new UInt16().loadFromBytes(rawMessage[currentPosition],rawMessage[currentPosition+1]);
		if (firstTwoBytes.getValue()>=COMPRESS_CONTANT_NUMBER) {			
			return true;
		}
		else {
			return false;
		}
		
	}
	

	private static String getCompressedName(byte[] rawMessage, int currentPosition) {
		UInt16 firstTwoBytes = new UInt16().loadFromBytes(rawMessage[currentPosition],rawMessage[currentPosition+1]);
		UInt16 nameStartByte = new UInt16(firstTwoBytes.getValue()-COMPRESS_CONTANT_NUMBER);
		return DomainConvert.decodeDNS(rawMessage,nameStartByte.getValue());
	}

// Original function	
//	public static String decodeDNS(byte [] encodedDomain, int startIndex) {
//		int passed = startIndex;
//		String result = "";
//		while(true) {
//		int size = (int) encodedDomain[passed];
//		if (size == 0) 
//		{
//			return result.substring(0,result.length()-1);
//		}
//		else {
//		for (int i = passed+1; i < passed+size+1; i++) {
//			result +=(char)encodedDomain[i];
//		}
//		passed += size+1;
//		result += ".";
//		}
//		}
//	}
	
	
	public static int getIndexOfLastByteOfName(byte[] wholeAnswerSection, int start) {
		 int position = start;
		 while(true) {
			 if ((int) wholeAnswerSection[position] == 0) {
				return position;
			}
			 else {
				 if((int) wholeAnswerSection[position] !=1) {
						if(isDnsNameCompressed(wholeAnswerSection,position)) {
							return position+1;
							}
				 position +=(int) wholeAnswerSection[position] +1;
				 }
			 }
	 	}
	}
	
    public static boolean isValidDomainName(String domainName) {
        return pDomainNameOnly.matcher(domainName).find();
        
    }
}
