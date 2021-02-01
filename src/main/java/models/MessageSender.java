package models;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import enums.APPLICATION_PROTOCOL;
import enums.Qcount;
import enums.TRANSPORT_PROTOCOL;

public class MessageSender {
	private Header header;
	private ArrayList<Request> requests;
	private TRANSPORT_PROTOCOL transport_protocol;
	private APPLICATION_PROTOCOL application_protocol;
	private static final int DNS_PORT = 53;
	private byte[] messageAsBytes;
	private String resolverIP;
	private InetAddress ip;
	private int size;
	private Socket socket;
	public MessageSender(boolean recursion, boolean dnssec, String domain, Qcount[] types,
			TRANSPORT_PROTOCOL transport_protocol, APPLICATION_PROTOCOL application_protocol,String resolverIP) {

		try {
			requests = new ArrayList<Request>();
			header = new Header(true, true, types.length);
			size = Header.getSize();
			for (Qcount qcount : types) {
				Request r = new Request(domain, qcount);
				requests.add(r);
				size += r.getSize();
			}
			this.resolverIP = resolverIP;
			this.transport_protocol = transport_protocol;
			this.application_protocol = application_protocol;
			this.ip =InetAddress.getByName(resolverIP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private void dnsOverUDP() throws Exception {
		if (size>512) {
			throw new Exception("512");
		}
		try {
		messageToBytes();
		DatagramPacket datagramPacket = new DatagramPacket(messageAsBytes, messageAsBytes.length,ip,DNS_PORT);
		new DatagramSocket().send(datagramPacket);
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
	}

	private void dnsOverTcp() throws IOException {
		messageToBytes();
		
		socket = new Socket(ip,DNS_PORT);
		OutputStream output = socket.getOutputStream();
		output.write(messageAsBytes);
		InputStream input = socket.getInputStream();

		InputStreamReader reader = new InputStreamReader(input);
		char character = (char) reader.read();  // reads a single character
		System.out.println(character);
		socket.close();
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
}
