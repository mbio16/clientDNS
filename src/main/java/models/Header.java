package models;

import org.json.simple.JSONObject;

import enums.AUTHENTICATE_DATA;
import enums.Aa;
import enums.CHECKING_DISABLED;
import enums.OpCode;
import enums.Qr;
import enums.Ra;
import enums.Rcode;
import enums.Rd;
import enums.Tc;


	public class Header {
		private UInt16 				id;
		private Qr					qr;
		private OpCode 				opCode;
		private Aa		 			aa;
		private Tc					tc;
		private Rd		 			rd;
		private Ra 					ra;
		private CHECKING_DISABLED 	cd;
		private AUTHENTICATE_DATA	ad;
		private Rcode		rCode;
		private UInt16		QdCount;
		private UInt16		AnCount;
		private UInt16		NsCount;
		private UInt16		ArCount;
		private static final int size = 12;
		
		private static final String ID_KEY="Id";
		private static final String QR_KEY="Reply";
		private static final String OPCODE_KEY="Opcode";
		private static final String AA_KEY="Authoritative answer";
		private static final String TC_KEY="Fragmented";
		private static final String RD_KEY="Recursion";
		private static final String CHECKING_DISABLED_KEY = "Checking disabled";
		private static final String AUTHENTICATE_DATA__KEY = "Authenticate data";
		private static final String RCODE_KEY="Response code";
		private static final String QDCOUNT_KEY="Number of questions";
		private static final String ANCOUNT_KEY="Number of answers";
		private static final String NSCOUNT_KEY="Number of authority answers";
		private static final String ARCOUNT_KEY="Number of additional records";
		public Header(boolean recursion, boolean dnssec, int numberOfQueries) {
			id = new UInt16().generateRandom();
			//id = new UInt16()
			
			qr = Qr.REQUEST;
			opCode = OpCode.QUERY;
			aa = Aa.NON_AUTHORITATIVE;
			tc = Tc.NON_FRAGMENTED;
			rd = Rd.getTypeByCode(recursion);
			ra = Ra.RECURSION_NON_AVAIBLE;
			ad = AUTHENTICATE_DATA.getTypeByCode(dnssec);
			cd = CHECKING_DISABLED.getTypeByCode(!dnssec);
			rCode = Rcode.NO_ERROR;
			QdCount = new UInt16(numberOfQueries);
			AnCount = new UInt16(0);
			NsCount = new UInt16(0);
			ArCount = new UInt16(0);
		}
		public Header() {};
		public byte[] getHaderAsBytes(){
			boolean opcodeBoolean [] = DataTypesConverter.byteToBoolArr(opCode.code, 4);
			boolean sub1 [] = {rd.code,tc.code,aa.code,opcodeBoolean[0],opcodeBoolean[1],opcodeBoolean[2],opcodeBoolean[3],qr.code};
			boolean rcodeBoolean [] = DataTypesConverter.byteToBoolArr(rCode.code, 4);
			boolean sub2 [] = {rcodeBoolean[0],rcodeBoolean[1],rcodeBoolean[2],rcodeBoolean[3],cd.code,ad.code,false,ra.code};
			
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
					+ ", ra=" + ra + ", cd=" + cd + ", ad=" + ad + ", rCode=" + rCode
					+ ", QdCount=" + QdCount.getValue() + ", AnCount=" + AnCount.getValue() + ", NsCount=" + NsCount.getValue() + ", ArCount=" + ArCount.getValue()
					+ "]";
		}
		
		@SuppressWarnings("unchecked")
		public JSONObject getAsJson() {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(ID_KEY,id.getValue());
			jsonObject.put(QR_KEY,qr.code);
			jsonObject.put(OPCODE_KEY,opCode);
			jsonObject.put(AA_KEY,aa.code);
			jsonObject.put(TC_KEY,tc.code);
			jsonObject.putRD_KEY="Recursion";
			private static final String CHECKING_DISABLED_KEY = "Checking disabled";
			private static final String AUTHENTICATE_DATA__KEY = "Authenticate data";
			private static final String RCODE_KEY="Response code";
			private static final String QDCOUNT_KEY="Number of questions";
			private static final String ANCOUNT_KEY="Number of answers";
			private static final String NSCOUNT_KEY="Number of authority answers";
			private static final String ARCOUNT_KEY
		}
		public Header parseHead(byte[] byteHead) {
			//id
			this.id =  new UInt16().loadFromBytes(byteHead[0],byteHead[1]);
			
			//second byte - first in flags
			boolean [] pom1 = DataTypesConverter.byteToBoolArr(byteHead[2], 8);
			this.rd = Rd.getTypeByCode(pom1[0]);
			this.tc = Tc.getTypeByCode(pom1[1]);
			this.aa = Aa.getTypeByCode(pom1[2]);
			boolean [] opcode = {pom1[3],pom1[4],pom1[5],pom1[6]};
			this.opCode = OpCode.getTypeByCode(DataTypesConverter.booleanArrayAsbyte(opcode));
			this.qr = Qr.getTypeByCode(pom1[7]);
			
			//second byte in flags
			boolean [] pom2 = DataTypesConverter.byteToBoolArr(byteHead[3], 8);
			boolean [] rcodeBoolean = {pom2[0],pom2[1],pom2[2],pom2[3]};
			this.rCode = Rcode.getTypeByCode(DataTypesConverter.booleanArrayAsbyte(rcodeBoolean));
			this.cd = CHECKING_DISABLED.getTypeByCode(pom2[4]);
			this.ad = AUTHENTICATE_DATA.getTypeByCode(pom2[5]);
			this.ra = Ra.getTypeByCode(pom2[7]); 
			
			this.QdCount = new UInt16().loadFromBytes(byteHead[4],byteHead[5]);
			this.AnCount = new UInt16().loadFromBytes(byteHead[6],byteHead[7]);
			this.NsCount = new UInt16().loadFromBytes(byteHead[8],byteHead[9]);
			this.ArCount = new UInt16().loadFromBytes(byteHead[10],byteHead[11]);
			return this;
		}
		
		public static int getSize() {
			return size;
		}
		public UInt16 getId() {
			return id;
		}
		public UInt16 getQdCount() {
			return QdCount;
		}
		public UInt16 getAnCount() {
			return AnCount;
		}
		public UInt16 getNsCount() {
			return NsCount;
		}
		public UInt16 getArCount() {
			return ArCount;
		}
		
		
}
