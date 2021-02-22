package models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.gson.GsonBuilder;
import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import exceptions.MessageTooBigForUDPException;
import exceptions.NotValidDomainNameException;
import exceptions.NotValidIPException;
import exceptions.TimeOutException;
import javafx.scene.control.TreeItem;

public class MessageSender {
	private Header header;
	private ArrayList<Request> requests;
	private TRANSPORT_PROTOCOL transport_protocol;
	private APPLICATION_PROTOCOL application_protocol;
	private static final int DNS_PORT = 53;
	private byte[] messageAsBytes;
	//private String resolverIP;
	private InetAddress ip;
	private int size;
	private Socket socket;
	private int messagesSent;
	private byte  [] recieveReply;
	private long timeElapsed;
	private boolean rrRecords;
	private TreeItem<String> root;
	
	private static final int MAX_MESSAGES_SENT=3;
	private static final int TIME_OUT_MILLIS = 3000;
	public static final int MAX_UDP_SIZE = 1232;
	private static final String KEY_HEAD="Head";
	private static final String KEY_QUERY="Questions";
	private static final String KEY_REQUEST = "Request";
	private static Logger LOGGER = Logger.getLogger(DomainConvert.class.getName());
	public MessageSender(boolean recursion, boolean dnssec,boolean rrRecords, String domain, Q_COUNT[] types,
			TRANSPORT_PROTOCOL transport_protocol, APPLICATION_PROTOCOL application_protocol,String resolverIP)throws  NotValidIPException, UnsupportedEncodingException, NotValidDomainNameException, UnknownHostException   {
			requests = new ArrayList<Request>();
			header = new Header(true, true, types.length, rrRecords);
			size = Header.getSize();
			addRequests(types, domain);
			//this.resolverIP = resolverIP;
			this.transport_protocol = transport_protocol;
			this.application_protocol = application_protocol;
			this.ip =InetAddress.getByName(resolverIP);
			this.messagesSent = 0;
			this.recieveReply = new byte [512];
			this.rrRecords = rrRecords;
	}
	
	public TreeItem<String> getAsTreeItem() {
		root = new TreeItem<String>(KEY_REQUEST);
		root.getChildren().add(header.getAsTreeItem());
		addRequestToTreeItem();
		if (rrRecords) {
			TreeItem<String> optRecord = new TreeItem<String>(MessageParser.KEY_ADDITIONAL_RECORDS);
			optRecord.getChildren().add(Response.getOptAsTreeItem());
			root.getChildren().add(optRecord);
		}
		return root;
	}
	
	private void addRequestToTreeItem() {
		TreeItem<String> subRequest = new TreeItem<String>(KEY_QUERY);
		if(requests.size()>0) {
			for (Request request: requests) {
				subRequest.getChildren().add(request.getAsTreeItem());
			}
			root.getChildren().add(subRequest);
		}
	}
	
	
	private void addRequests(Q_COUNT [] types, String domain)  throws NotValidIPException, UnsupportedEncodingException, NotValidDomainNameException{
		for (Q_COUNT qcount : types) {
			Request r = new Request(domain, qcount);
			requests.add(r);
			size += r.getSize();
		}
	}
	public void send() throws TimeOutException, IOException, MessageTooBigForUDPException {
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
			break;
		case LLMR:
			break;
		case DOH:
			break;
		

		default:
			break;
		}
	}

	@SuppressWarnings("resource")
	private void dnsOverUDP() throws TimeOutException, IOException, MessageTooBigForUDPException {
		if(size>MAX_UDP_SIZE) throw new MessageTooBigForUDPException();
		messagesSent = 0;
		messageToBytes();
		DatagramSocket datagramSocket = new DatagramSocket();
		boolean run = true;
		boolean exception = false;
		while (run) {
			try {
				if(messagesSent==MAX_MESSAGES_SENT) {
					throw new TimeOutException();
				}
			DatagramPacket responsePacket = new DatagramPacket(recieveReply, recieveReply.length);
			DatagramPacket datagramPacket = new DatagramPacket(messageAsBytes, messageAsBytes.length,ip,DNS_PORT);
			datagramSocket.setSoTimeout(TIME_OUT_MILLIS);
			Instant start = Instant.now(); 
			datagramSocket.send(datagramPacket);
			datagramSocket.receive(responsePacket);
			Instant finish = Instant.now();
			timeElapsed = Duration.between(start, finish).toMillis();
			run = false;	
			}
			catch (SocketTimeoutException e) {
	            LOGGER.warning("Time out for the: " + (messagesSent+1) + " message");
	            if(messagesSent==MAX_MESSAGES_SENT) {
	            	timeElapsed = 0;
	            	socket.close();
	            }
	        }
			messagesSent++;
		}
		if(exception) {
			throw new TimeOutException();
		}
		
	}

	private void dnsOverTcp() throws IOException {
		messageToBytes();
		messagesSent = 1;
		Instant start = Instant.now();
		
		socket = new Socket(ip,DNS_PORT);
		OutputStream output = socket.getOutputStream();
		output.write(messageAsBytes);
 
		InputStream input = socket.getInputStream();
		byte [] recieve = input.readAllBytes();
		socket.close();
		
		Instant finish = Instant.now();
		timeElapsed = Duration.between(start,finish).toMillis();
		
		removeFirstTwoBytesFromReply(recieve);
				
	}
	private void removeFirstTwoBytesFromReply(byte [] recieve) {
		this.recieveReply = new byte [recieve.length-2];
		int j=0;
		for (int i = 2; i < recieve.length; i++) {
			recieveReply[j] = recieve[i];
			j++;
		}
	}
	private void messageToBytes() {
		int curentIndex = 0;
		if (rrRecords) {
			size += new Response().getDnssecAsBytes().length;
		}
		if(transport_protocol == TRANSPORT_PROTOCOL.TCP) {
			
			this.messageAsBytes = new byte [size+2];
			UInt16 tcpSize = new UInt16(size);
			curentIndex = 2;
			this.messageAsBytes[1] = tcpSize.getAsBytes()[0];
			this.messageAsBytes[0] = tcpSize.getAsBytes()[1];
		}
		else {
			this.messageAsBytes = new byte [size];
		}
		
		
		byte head [] = header.getHaderAsBytes();
		for (int i = 0; i < head.length; i++) {
			this.messageAsBytes[curentIndex] = head[i];
			curentIndex++;
		}
		for (Request r : requests) {
			byte requestBytes [] = r.getRequestAsBytes();
			for (int i = 0; i < requestBytes.length; i++) {
				this.messageAsBytes[curentIndex] = requestBytes[i];
				curentIndex++;
			}
		}
		byte opt [] = new Response().getDnssecAsBytes();
		int j = 0;
		for (int i = curentIndex; i < size; i++) {
			this.messageAsBytes[i] = opt[j];
			j++;
		}
		
		
	}

	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonObject.put(KEY_HEAD,header.getAsJson());
		
		for (Request request : requests) {
			jsonArray.add(request.getAsJson());
		}
		jsonObject.put(KEY_QUERY, jsonArray);
		return jsonObject;
	}
	
	public String getAsJsonString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(getAsJson());
	}
	public Header getHeader() {
		return header;
	}

	public int getMessagesSent() {
		return messagesSent;
	}

	public byte[] getRecieveReply() {
		return recieveReply;
	}

	public long getTimeElapsed() {
		return timeElapsed;
	}
	
	public void printStats() {
		System.out.println("number of tries: " + this.messagesSent);
		System.out.println("Time to send: " + this.timeElapsed);
	}
	
}
