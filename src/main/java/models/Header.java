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

		public byte[] getHaderAsBytes(){
			boolean opcodeBoolean [] = DataTypesConverter.byteToBoolArr(opCode, 4);
			boolean sub1 [] = {rd,tc,aa,opcodeBoolean[0],opcodeBoolean[1],opcodeBoolean[2],opcodeBoolean[3],qr};
			boolean rcodeBoolean [] = DataTypesConverter.byteToBoolArr(rCode, 4);
			boolean sub2 [] = {rcodeBoolean[0],rcodeBoolean[1],rcodeBoolean[2],rcodeBoolean[3],cd,ad,false,ra};
			
			byte result [] = {
					id.getAsBytes()[1],
					id.getAsBytes()[0],
					DataTypesConverter.booleanArrayAsbyte(sub1),
					DataTypesConverter.booleanArrayAsbyte(sub2),
					QdCount.getAsBytes()[1],
					QdCount.getAsBytes()[0],
					AnCount.getAsBytes()[1],
					AnCount.getAsBytes()[0],
					NsCount.getAsBytes()[1],
					NsCount.getAsBytes()[0],
					ArCount.getAsBytes()[1],
					ArCount.getAsBytes()[0]
			};
			
			return result;
		}
		
		
		@Override
		public String toString() {
			return "Header [id=" + id.getValue() + ", qr=" + qr + ", opCode=" + opCode + ", aa=" + aa + ", tc=" + tc + ", rd=" + rd
					+ ", ra=" + ra + ", cd=" + cd + ", ad=" + ad + ", nonathethicate=" + nonathethicate + ", rCode=" + rCode
					+ ", QdCount=" + QdCount.getValue() + ", AnCount=" + AnCount.getValue() + ", NsCount=" + NsCount.getValue() + ", ArCount=" + ArCount.getValue()
					+ "]";
		}
		
		public Header parseHead(byte[] byteHead) {
			//id
			this.id =  new UInt16().loadFromBytes(byteHead[0],byteHead[1]);
			
			//second byte - first in flags
			boolean [] pom1 = DataTypesConverter.byteToBoolArr(byteHead[2], 8);
			this.rd = pom1[0];
			this.tc = pom1[1];
			this.aa = pom1[2];
			boolean [] opcode = {pom1[3],pom1[4],pom1[5],pom1[6]};
			this.opCode = DataTypesConverter.booleanArrayAsbyte(opcode);
			this.qr = pom1[7];
			
			//second byte in flags
			boolean [] pom2 = DataTypesConverter.byteToBoolArr(byteHead[3], 8);
			boolean [] rcodeBoolean = {pom2[0],pom2[1],pom2[2],pom2[3]};
			this.rCode = DataTypesConverter.booleanArrayAsbyte(rcodeBoolean);
			this.cd = pom2[4];
			this.ad = pom2[5];
			this.ra = pom2[7]; // indexy postupnì dolu ne nahoru (opravit)
			
			this.QdCount = new UInt16().loadFromBytes(byteHead[4],byteHead[5]);
			this.AnCount = new UInt16().loadFromBytes(byteHead[6],byteHead[7]);
			this.NsCount = new UInt16().loadFromBytes(byteHead[8],byteHead[9]);
			this.ArCount = new UInt16().loadFromBytes(byteHead[10],byteHead[11]);
			return this;
		}
		
		
}
