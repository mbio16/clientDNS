package models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.GsonBuilder;
import enums.APPLICATION_PROTOCOL;
import enums.IP_PROTOCOL;
import enums.Q_COUNT;
import enums.RESPONSE_MDNS_TYPE;
import enums.TRANSPORT_PROTOCOL;
import exceptions.CouldNotUseHoldConnectionException;
import exceptions.HttpCodeException;
import exceptions.MessageTooBigForUDPException;
import exceptions.NotValidDomainNameException;
import exceptions.NotValidIPException;
import exceptions.OtherHttpException;
import exceptions.TimeoutException;
import javafx.scene.control.TreeItem;

public class MessageSender {
	private Header header;
	private ArrayList<Request> requests;
	private TRANSPORT_PROTOCOL transport_protocol;
	private APPLICATION_PROTOCOL application_protocol;
	private static final int DNS_PORT = 53;
	private byte[] messageAsBytes;
	private int byteSizeQuery;
	private String domainAsString;
	private InetAddress ip;
	private String resolver;
	private int size;
	private Socket socket;
	private int messagesSent;
	private byte[] recieveReply;
	private boolean rrRecords;
	private TreeItem<String> root;
	private long startTime;
	private long stopTime;
	private boolean dnssec;
	private TCPConnection tcp;
	private IP_PROTOCOL ipProtocol;
	private RESPONSE_MDNS_TYPE mdnsType;
	private boolean mdnsDnssecSignatures;
	private boolean closeConnection;
	private Q_COUNT [] qcountTypes;
	private String httpRequest;
	private JSONObject httpResponse;
	private static final int MAX_MESSAGES_SENT = 3;
	private static final int TIME_OUT_MILLIS = 2000;
	public static final int MAX_UDP_SIZE = 1232;
	private static final String IPv4_MDNS="224.0.0.251";
	private static final String IPv6_MDNS="ff02::fb";
	private static final int MDNS_PORT = 5353;
	private static final String KEY_HEAD = "Head";
	private static final String KEY_QUERY = "Questions";
	private static final String KEY_REQUEST = "Request";
	public static final String KEY_ADDITIONAL_RECORDS = "Aditional records";
	private static final String KEY_LENGHT="Lenght";
	private static final String [] httpRequestParamsName = new String [] {
			"name",
			"type",
			"do",
			"cd"
	};
	private static Logger LOGGER = Logger.getLogger(DomainConvert.class.getName());

	public MessageSender(boolean recursion, boolean dnssec, boolean rrRecords, String domain, Q_COUNT[] types,
			TRANSPORT_PROTOCOL transport_protocol, APPLICATION_PROTOCOL application_protocol, String resolverIP)
			throws NotValidIPException, UnsupportedEncodingException, NotValidDomainNameException, UnknownHostException {
		requests = new ArrayList<Request>();
		header = new Header(recursion, dnssec, types.length, rrRecords);
		size = Header.getSize();
		addRequests(types, checkAndStripFullyQualifyName(domain));
		// this.resolverIP = resolverIP;
		this.transport_protocol = transport_protocol;
		this.application_protocol = application_protocol;
		if (application_protocol != APPLICATION_PROTOCOL.DOH ) {
			this.ip = InetAddress.getByName(resolverIP);
		}
		this.resolver = resolverIP;
		this.recieveReply = new byte[1232];
		this.rrRecords = rrRecords;
		this.dnssec = dnssec;
		this.qcountTypes = types;
		this.domainAsString = domain;
		messagesSent = 0;
		tcp = null;
		closeConnection = true;
	}

	public MessageSender(boolean mdnsDnssecSignatures,String domain, Q_COUNT[] types, IP_PROTOCOL ipProtocol, RESPONSE_MDNS_TYPE  mdnsType) throws UnsupportedEncodingException, NotValidIPException, NotValidDomainNameException {
		this.application_protocol = APPLICATION_PROTOCOL.MDNS;
		this.ipProtocol = ipProtocol;
		this.mdnsDnssecSignatures = mdnsDnssecSignatures;
		requests = new ArrayList<Request>();
		header = new Header(types.length);
		this.size = Header.getSize();
		this.mdnsType = mdnsType;
		addRequests(types, checkAndStripFullyQualifyName(domain), mdnsType);
		this.messagesSent = 0;
		this.recieveReply = new byte [1232];
	}
	private String checkAndStripFullyQualifyName(String domain) {
		if (domain.endsWith(".")) {
			System.out.println("Striping .");
			return domain.substring(0, domain.length() - 1);
		} else {
			return domain;
		}
	}

	public TreeItem<String> getAsTreeItem() {
		root = new TreeItem<String>(KEY_REQUEST);
		root.getChildren().add(header.getAsTreeItem());
		addRequestToTreeItem();
		//OPT in DNS
		if (rrRecords) {
			TreeItem<String> optRecord = new TreeItem<String>(MessageParser.KEY_ADDITIONAL_RECORDS);
			optRecord.getChildren().add(Response.getOptAsTreeItem(true,false));
			root.getChildren().add(optRecord);
		}
		if(mdnsType != null){
			TreeItem<String> optRecord = new TreeItem<String>(MessageParser.KEY_ADDITIONAL_RECORDS);
			optRecord.getChildren().add(Response.getOptAsTreeItem(mdnsDnssecSignatures,true));
			root.getChildren().add(optRecord);
		}
		if(transport_protocol == TRANSPORT_PROTOCOL.TCP) {
			TreeItem<String> tcpTreeItem = new TreeItem<String>("");
			tcpTreeItem.getChildren().add(new TreeItem<String>(KEY_LENGHT + ": " + (byteSizeQuery - 2)));
			tcpTreeItem.getChildren().add(root);
			return tcpTreeItem;
			
		}
		return root;
	}

	private void addRequestToTreeItem() {
		TreeItem<String> subRequest = new TreeItem<String>(KEY_QUERY);
		if (requests.size() > 0) {
			for (Request request : requests) {
				subRequest.getChildren().add(request.getAsTreeItem());
			}
			root.getChildren().add(subRequest);
		}
	}

	private void addRequests(Q_COUNT[] types, String domain)
			throws NotValidIPException, UnsupportedEncodingException, NotValidDomainNameException {
		for (Q_COUNT qcount : types) {
			Request r = new Request(domain, qcount);
			requests.add(r);
			size += r.getSize();
		}
	}

	private void addRequests(Q_COUNT[] types, String domain, RESPONSE_MDNS_TYPE mdnsType)
			throws NotValidIPException, UnsupportedEncodingException, NotValidDomainNameException {
		for (Q_COUNT qcount : types) {
			Request r = new Request(domain,qcount,mdnsType);
			requests.add(r);
			size += r.getSize();
		}
	}
	public void send()
			throws TimeoutException, IOException, MessageTooBigForUDPException, CouldNotUseHoldConnectionException, HttpCodeException, OtherHttpException,ParseException {
		switch (application_protocol) {
		case DNS:
			switch (transport_protocol) {
			case TCP:
				dnsOverTcp();
				break;
			case UDP:
				dnsOverUDP();
			default:
				break;
			}
			break;
		case MDNS:
			mdns();
			break;
		case LLMR:
			break;
		case DOH:
			doh();
			break;
		default:
			break;
		}
	}
	private void doh() throws HttpCodeException,OtherHttpException,ParseException {
	try {
	String httpsDomain = resolver.split("/")[0];
	CloseableHttpResponse response;
	;
	String [] values = new String [] {
			domainAsString,
			qcountAsString(),
			""+ rrRecords,
			""+ !dnssec
	};
	messagesSent = 1;
	String uri  = addParamtoUris(resolver, httpRequestParamsName, values);
	System.out.println(uri);
	switch (httpsDomain) {
	case "dns.google":
		response = sendAndRecieveDoH(uri, httpsDomain,false);
		break;
	case "cloudflare-dns.com":
		response = sendAndRecieveDoH(uri,httpsDomain,true);
		break;
	default:
		response = null;
		break;
	}

       if (response.getStatusLine().getStatusCode() == 200) {
        	 String content = EntityUtils.toString(response.getEntity());
        	 parseResponseDoh(content);
        	 System.out.println(content);
         }
         else {
			throw new HttpCodeException(response.getStatusLine().getStatusCode());
		}
	 }
	catch (HttpCodeException e) {
		throw e;
	}
	catch (ParseException e) {
		throw e;
	}
	 catch (Exception e) {
		 e.printStackTrace();
		throw new OtherHttpException();
	}
	
	}
	private String qcountAsString(){
		String result = "";
		for (int i = 0; i < qcountTypes.length; i++) {
			if(i==0) {
				result += qcountTypes[i];
			}
			else {
				result += ","+qcountTypes[i];
			}
		}
		return result;
	}
	
	private CloseableHttpResponse sendAndRecieveDoH(String uri,String host,boolean httpGet) throws ClientProtocolException, IOException {
		if(httpGet) {
			HttpGet request = new HttpGet(uri);
			request.addHeader("Accept","application/dns-json");
			request.addHeader("Accept-Encoding","gzip, deflate, br");
			request.addHeader("User-Agent", "Client-DNS");
			request.addHeader("Host",host);
			httpRequestAsString(request);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			startTime = System.nanoTime();
			CloseableHttpResponse response = httpClient.execute(request);
			stopTime = System.nanoTime();
			return response;
		}
		else {
			HttpPost request = new HttpPost(uri);
			request.addHeader("Accept","application/dns-json");
			request.addHeader("Accept-Encoding","gzip, deflate, br");
			request.addHeader("User-Agent", "Client-DNS");
			request.addHeader("Host",host);
			httpRequestAsString(request);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			startTime = System.nanoTime();
			System.out.println(getDoHRequest());
			CloseableHttpResponse response = httpClient.execute(request);
			stopTime = System.nanoTime();
			return response;
		}

	}
	private void httpRequestAsString(HttpRequestBase request) {
		String result = request.toString() + "\n";
		for (org.apache.http.Header httpHeader :request.getAllHeaders()) {
			result +=  httpHeader.toString() + "\n";
		}
		httpRequest = result;
	}
	public String getDoHRequest() {
		return httpRequest;
	}
	
	@SuppressWarnings("resource")
	private void mdns() throws UnknownHostException, TimeoutException, BindException{
		messageToBytesMDNS();
		messagesSent = 1;
		InetAddress group;
		DatagramSocket unicastSocket; 
		if(ipProtocol==IP_PROTOCOL.IPv4) {
			group = InetAddress.getByName(IPv4_MDNS);
		}
		else {
			group = InetAddress.getByName(IPv6_MDNS);
		}

		while(true) {
		try {
		 MulticastSocket socket = new MulticastSocket(MDNS_PORT);
		 socket.joinGroup(group);
		 DatagramPacket datagramPacket = new DatagramPacket(messageAsBytes, messageAsBytes.length,
                 group, MDNS_PORT);
		 startTime = System.nanoTime();
		 socket.setSoTimeout(TIME_OUT_MILLIS);
		 socket.send(datagramPacket);
		 DatagramPacket recievePacket = new DatagramPacket(recieveReply, recieveReply.length);
		if(mdnsType == RESPONSE_MDNS_TYPE.RESPONSE_UNICAST) {
			socket.leaveGroup(group);
			socket.close();
			unicastSocket = new DatagramSocket(MDNS_PORT);
			unicastSocket.receive(recievePacket);
			unicastSocket.close();
			socket.close();
			return;
			
		}
		else {
		socket.receive(recievePacket);
		socket.receive(recievePacket);
		stopTime = System.nanoTime();
		socket.leaveGroup(group);
		socket.close();
		return;
		}
		}
		catch (BindException e) {
			throw new BindException();
		}
		catch (Exception e) {
			e.printStackTrace();
			if (messagesSent < 3) {
				messagesSent++;
			}	
			else {
				throw new TimeoutException();
				}
			}
		}
		
		
	}
	private void dnsOverUDP() throws TimeoutException, IOException, MessageTooBigForUDPException {
		if (size > MAX_UDP_SIZE)
			throw new MessageTooBigForUDPException();
		messagesSent = 1;
		messageToBytes();
		DatagramSocket datagramSocket = new DatagramSocket();
		boolean run = true;
		boolean exception = false;
		while (run) {
			try {
				if (messagesSent == MAX_MESSAGES_SENT) {
					throw new TimeoutException();
				}


				DatagramPacket responsePacket = new DatagramPacket(recieveReply, recieveReply.length);
				DatagramPacket datagramPacket = new DatagramPacket(messageAsBytes, messageAsBytes.length, ip, DNS_PORT);
				datagramSocket.setSoTimeout(TIME_OUT_MILLIS);
				startTime = System.nanoTime();

				datagramSocket.send(datagramPacket);
				datagramSocket.receive(responsePacket);

				stopTime = System.nanoTime();
				datagramSocket.close();
				run = false;
			} catch (SocketTimeoutException e) {
				LOGGER.warning("Time out for the: " + (messagesSent + 1) + " message");
				if (messagesSent == MAX_MESSAGES_SENT) {
					socket.close();
				}
			}
			messagesSent++;
		}
		if (exception) {
			throw new TimeoutException();
		}

	}

	private void dnsOverTcp() throws TimeoutException, CouldNotUseHoldConnectionException {
		messageToBytes();
		try {
			messagesSent = 1;
			startTime = System.nanoTime();
			if (tcp == null) {
				tcp = new TCPConnection(ip);
			}
			this.recieveReply = tcp.send(this.messageAsBytes, ip, this.closeConnection);
			stopTime = System.nanoTime();
		} catch (IOException e) {
			throw new TimeoutException();
		}
	}

	public void closeTCPConnection() throws IOException {
		tcp.closeAll();
	}
	
	private  void parseResponseDoh(String response) throws ParseException {
		JSONParser parser = new JSONParser();  
		this.httpResponse = (JSONObject) parser.parse(response);  
	}
	private String addParamtoUris(String uri,String [] paramNames, String [] values) {
		
		String result = "https://" + uri +"?";
		for (int i = 0; i < values.length; i++) {
			if (i==0) {
				result += paramNames[i] + "=" + values [i];
			}
			else {
			result += "&" + paramNames[i] + "=" + values [i];
			}
		}
		return result;

	}
	private void messageToBytes() {
		int curentIndex = 0;
		if (rrRecords) {
			size += new Response().getDnssecAsBytes().length;
		}
		if (transport_protocol == TRANSPORT_PROTOCOL.TCP) {

			this.messageAsBytes = new byte[size + 2];
			UInt16 tcpSize = new UInt16(size);
			curentIndex = 2;
			this.messageAsBytes[1] = tcpSize.getAsBytes()[0];
			this.messageAsBytes[0] = tcpSize.getAsBytes()[1];
		} else {
			this.messageAsBytes = new byte[size];
		}

		byte head[] = header.getHaderAsBytes();
		for (int i = 0; i < head.length; i++) {
			this.messageAsBytes[curentIndex] = head[i];
			curentIndex++;
		}
		for (Request r : requests) {
			byte requestBytes[] = r.getRequestAsBytes();
			for (int i = 0; i < requestBytes.length; i++) {
				this.messageAsBytes[curentIndex] = requestBytes[i];
				curentIndex++;
			}
		}
		byte opt[] = new Response().getDnssecAsBytes();
		int j = 0;
		for (int i = curentIndex; i < size; i++) {
			this.messageAsBytes[i] = opt[j];
			j++;
		}
		byteSizeQuery = messageAsBytes.length;

	}
	private void messageToBytesMDNS() {
		int curentIndex = 0;
		size += new Response().getDnssecAsBytesMDNS(mdnsDnssecSignatures).length;
		this.messageAsBytes = new byte [size];
		byte head [] = header.getHaderAsBytes();
		for (int i = 0; i < head.length; i++) {
			this.messageAsBytes[curentIndex] = head[i];
			curentIndex++;
		}
		for (Request r : requests) {
			byte requestBytes[] = r.getRequestAsBytes();
			for (int i = 0; i < requestBytes.length; i++) {
				this.messageAsBytes[curentIndex] = requestBytes[i];
				curentIndex++;
			}
		}
		byte opt[] = new Response().getDnssecAsBytesMDNS(mdnsDnssecSignatures);
		int j = 0;
		for (int i = curentIndex; i < size; i++) {
			this.messageAsBytes[i] = opt[j];
			j++;
		}
		byteSizeQuery = messageAsBytes.length;
	}
	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonObject.put(KEY_HEAD, header.getAsJson());

		for (Request request : requests) {
			jsonArray.add(request.getAsJson());
		}
		jsonObject.put(KEY_QUERY, jsonArray);
		if (transport_protocol == TRANSPORT_PROTOCOL.TCP) jsonObject.put(KEY_LENGHT, (byteSizeQuery -2));
		
		//opt record
		if (header.getArCount().getValue()==1) {
			boolean mdns = (mdnsType != null);
			if(mdns)
				jsonObject.put(KEY_ADDITIONAL_RECORDS,Response.getOptRequestAsJson(mdnsDnssecSignatures, mdns));
			else
				jsonObject.put(KEY_ADDITIONAL_RECORDS,Response.getOptRequestAsJson(true, mdns));
			}
		return jsonObject;
	}


	public String getAsJsonString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(getAsJson());
	}

	public Header getHeader() {
		return header;
	}

	public int getMessageSent() {
		return messagesSent;
	}

	public byte[] getRecieveReply() {
		return recieveReply;
	}

	public double getTimeElapsed() {
		double h = (stopTime - startTime) / 1000000.00;
		return Math.round(h * 100) / 100.0;
	}

	public int getByteSizeQuery() {
		return byteSizeQuery;
	}

	public void printStats() {
		System.out.println("Time to send: " + getTimeElapsed());
	}

	public TCPConnection getTcp() {
		return tcp;
	}

	public void setTcp(TCPConnection tcp) {
		this.tcp = tcp;
	}

	public void setCloseConnection(boolean closeConnection) {
		this.closeConnection = closeConnection;
	}


	public JSONObject getHttpResponse() {
		return httpResponse;
	}

}
