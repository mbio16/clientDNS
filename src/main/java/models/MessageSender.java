package models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Logger;

import enums.APPLICATION_PROTOCOL;
import enums.Qcount;
import enums.TRANSPORT_PROTOCOL;
import exceptions.TimeOutException;

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
	private static final int MAX_MESSAGES_SENT=3;
	private static final int TIME_OUT_MILLIS = 3000;
	
	private static Logger LOGGER = Logger.getLogger(DomainConvert.class.getName());
	public MessageSender(boolean recursion, boolean dnssec, String domain, Qcount[] types,
			TRANSPORT_PROTOCOL transport_protocol, APPLICATION_PROTOCOL application_protocol,String resolverIP) throws Exception {
			requests = new ArrayList<Request>();
			header = new Header(true, true, types.length);
			size = Header.getSize();
			addRequests(types, domain);
			//this.resolverIP = resolverIP;
			this.transport_protocol = transport_protocol;
			this.application_protocol = application_protocol;
			this.ip =InetAddress.getByName(resolverIP);
			this.messagesSent = 0;
			this.recieveReply = new byte [512];
	}

	private void addRequests(Qcount [] types, String domain) throws Exception {
		for (Qcount qcount : types) {
			Request r = new Request(domain, qcount);
			requests.add(r);
			size += r.getSize();
		}
	}
	public void send() throws Exception {
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

	private void dnsOverUDP() throws TimeOutException, IOException {
		assert size>512 : "Too big  than512";
		messagesSent = 0;
		messageToBytes();
		DatagramSocket datagramSocket = new DatagramSocket();
		boolean run = true;
		while (run) {
			try {
				if(messagesSent==MAX_MESSAGES_SENT) return;
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
	            // timeout exception.
	            LOGGER.warning("Time out for the: " + (messagesSent+1) + " message");
	            if(messagesSent==MAX_MESSAGES_SENT) {
	            	timeElapsed = 0;
	            	socket.close();
	            	throw new TimeOutException();
	            }
	        }
			messagesSent++;
		}
		
		
	}

	private void dnsOverTcp() throws IOException {
		messageToBytes();
		
		socket = new Socket(ip,DNS_PORT);
		OutputStream output = socket.getOutputStream();
		output.write(messageAsBytes);
		InputStream input = socket.getInputStream();

		byte [] recieve = input.readAllBytes();
		removeFirstTwoBytesFromReply(recieve);
		socket.close();
		
		
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
