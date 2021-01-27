package models;

import enums.OpCode;
import enums.Rcode;

public class Header {
	private UInt16 		id;
	private boolean		qr;
	private byte 		opCode;
	private boolean 	aa;
	private boolean 	tc;
	private boolean 	rd;
	private boolean 	ra;
	private boolean 	cd;
	private boolean 	ad;
	private boolean 	nonathethicate;
	private byte		rCode;
	private UInt16		QdCount;
	private UInt16		AnCount;
	private UInt16		NsCount;
	private UInt16		ArCount;

	
	
	public Header(boolean recursion, boolean dnssec, int numberOfQueries) {
		id = new UInt16().generateRandom();
		//id = new UInt16()
		
		qr = false;
		opCode = OpCode.QUERY.code;
		aa = false;
		tc = false;
		rd = recursion;
		ra = false;
		ad = dnssec;
		cd = false;
		rCode = 0x00;
		QdCount = new UInt16(numberOfQueries);
		AnCount = new UInt16(0);
		NsCount = new UInt16(0);
		ArCount = new UInt16(0);
	}
	
	private boolean[] byteToBoolArr(byte j,int size) {
	    boolean boolArr[] = new boolean[8];
	    for(int i=0;i<8;i++) boolArr[i] = (j & (byte)(128 / Math.pow(2, i))) != 0;
	    return getSliceOfArray(boolArr, size);
	}
    private boolean[] getSliceOfArray(boolean[] arr,  int end) 
	{ 
		int start = 0;
		// Get the slice of the Array 
		boolean [] slice = new boolean[end - start]; 
		
		// Copy elements of arr to slice 
		for (int i = 0; i < slice.length; i++) { 
		slice[i] = arr[start + i]; 
		} 
		
		// return the slice
		return slice; 
		} 
    


	public byte[] getHaderAsBytes(){
		boolean opcodeBoolean [] = byteToBoolArr(opCode, 4);
		boolean sub1 [] = {rd,tc,aa,opcodeBoolean[0],opcodeBoolean[1],opcodeBoolean[2],opcodeBoolean[3],qr};
		boolean rcodeBoolean [] = byteToBoolArr(rCode, 4);
		boolean sub2 [] = {rcodeBoolean[0],rcodeBoolean[1],rcodeBoolean[2],rcodeBoolean[3],cd,ad,false,ra};
		
		byte result [] = {
				id.getAsBytes()[0],
				id.getAsBytes()[1],
				booleanArrayAsbyte(sub1),
				booleanArrayAsbyte(sub2),
				QdCount.getAsBytes()[0],
				QdCount.getAsBytes()[1],
				AnCount.getAsBytes()[0],
				AnCount.getAsBytes()[1],
				NsCount.getAsBytes()[0],
				NsCount.getAsBytes()[1],
				ArCount.getAsBytes()[0],
				ArCount.getAsBytes()[1]
		};
		
		return result;
	}
	private byte booleanArrayAsbyte(boolean [] boolArray){
		int res = 0;
		for (int i = 0; i < boolArray.length; i++) {
			if (boolArray[i]) {
				res +=(int) Math.pow(2, i);
			}
		}
		return (byte) res;
	}	
	
	@Override
	public String toString() {
		return "Header [id=" + id + ", qr=" + qr + ", opCode=" + opCode + ", aa=" + aa + ", tc=" + tc + ", rd=" + rd
				+ ", ra=" + ra + ", cd=" + cd + ", ad=" + ad + ", nonathethicate=" + nonathethicate + ", rCode=" + rCode
				+ ", QdCount=" + QdCount + ", AnCount=" + AnCount + ", NsCount=" + NsCount + ", ArCount=" + ArCount
				+ "]";
	}
	
	public void parseHead(byte[] byteHead) {
		//id
		id =  new UInt16().loadFromBytes(byteHead[1],byteHead[0]);
		
		//second byte - first in flags
		boolean [] pom1 = byteToBoolArr(byteHead[2], 8);
		rd = pom1[0];
		rd = pom1[1];
		tc = pom1[2];
		aa = pom1[3];
		boolean [] opcode = {pom1[4],pom1[5],pom1[6],pom1[7]};
		opCode = booleanArrayAsbyte(opcode);
		qr = pom1[7];
		
		//second byte in flags
		boolean [] pom2 = byteToBoolArr(byteHead[3], 8);
		boolean [] rcodeBoolean = {pom2[0],pom2[1],pom2[2],pom2[3]};
		rCode = booleanArrayAsbyte(rcodeBoolean);
		cd = pom2[4];
		ad = pom2[5];
		ra = pom2[7]; // indexy postupnì dolu ne nahoru (opravit)
		
		QdCount = new UInt16().loadFromBytes(byteHead[5],byteHead[4]);
		AnCount = new UInt16().loadFromBytes(byteHead[7],byteHead[6]);
		NsCount = new UInt16().loadFromBytes(byteHead[9],byteHead[8]);
		ArCount = new UInt16().loadFromBytes(byteHead[11],byteHead[10]);
		
	}
	
	
	
}
