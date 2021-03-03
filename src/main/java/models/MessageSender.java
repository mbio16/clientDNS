package models;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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
import exceptions.TimeoutException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
	private boolean rrRecords;
	private TreeItem<String> root;
	private long startTime;
	private long stopTime;
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
			header = new Header(recursion, dnssec, types.length, rrRecords);
			size = Header.getSize();
			addRequests(types, checkAndStripFullyQualifyName(domain));
			//this.resolverIP = resolverIP;
			this.transport_protocol = transport_protocol;
			this.application_protocol = application_protocol;
			this.ip =InetAddress.getByName(resolverIP);
			this.recieveReply = new byte [512];
			this.rrRecords = rrRecords;
			messagesSent = 0;
	}
	
	private String checkAndStripFullyQualifyName(String domain) {
		if(domain.endsWith(".")) {
			System.out.println("Striping .");
			return domain.substring(0,domain.length()-1);
		}
		else {
			return domain;
		}
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
	public void send() throws TimeoutException, IOException, MessageTooBigForUDPException {
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
	private void dnsOverUDP() throws TimeoutException, IOException, MessageTooBigForUDPException {
		if(size>MAX_UDP_SIZE) throw new MessageTooBigForUDPException();
		messagesSent = 0;
		messageToBytes();
		DatagramSocket datagramSocket = new DatagramSocket();
		boolean run = true;
		boolean exception = false;
		while (run) {
			try {
				if(messagesSent==MAX_MESSAGES_SENT) {
					throw new TimeoutException();
				}
				
			DatagramPacket responsePacket = new DatagramPacket(recieveReply, recieveReply.length);
			DatagramPacket datagramPacket = new DatagramPacket(messageAsBytes, messageAsBytes.length,ip,DNS_PORT);
			datagramSocket.setSoTimeout(TIME_OUT_MILLIS);
			startTime = System.nanoTime();
 
			datagramSocket.send(datagramPacket);
			datagramSocket.receive(responsePacket);
			stopTime = System.nanoTime();
			datagramSocket.close();
			
			run = false;	
			}
			catch (SocketTimeoutException e) {
				//Alert a = new Alert(AlertType.INFORMATION,"Timeout");
				//a.show();
	            LOGGER.warning("Time out for the: " + (messagesSent+1) + " message");
	            if(messagesSent==MAX_MESSAGES_SENT) {
	            	socket.close();
	            }
	        }
			messagesSent++;
		}
		if(exception) {
			throw new TimeoutException();
		}
		
	}

	private void dnsOverTcp() throws IOException {
		messageToBytes();
		startTime = System.nanoTime();
		try {
		socket = new Socket(ip,DNS_PORT);
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		output.write(messageAsBytes);
 
		InputStream input = socket.getInputStream();
		byte [] sizeRicieve = input.readNBytes(2);

		UInt16 messageSize = new UInt16().loadFromBytes(sizeRicieve[0],sizeRicieve[1]);
		System.out.println(messageSize.getValue());
		this.recieveReply = input.readNBytes(messageSize.getValue());

		output.close();
		input.close();
		socket.close();
		stopTime = System.nanoTime();
		}
		catch (IOException e) {
			throw new IOException();
		}
		finally {

			socket.close();
			System.out.println(socket.isConnected());
			socket = null;	
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

	public int getMessageSent() {
		return messagesSent;
	}

	public byte[] getRecieveReply() {
		return recieveReply;
	}

	public double getTimeElapsed() {
		double h = (stopTime-startTime)/1000000.00;
		return Math.round(h*100)/100.0;
	}
	
	public void printStats() {
		System.out.println("Time to send: " + getTimeElapsed());
	}
	
}
