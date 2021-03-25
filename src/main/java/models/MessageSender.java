package models;

import java.io.IOException;
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
import exceptions.CouldNotUseHoldConnectionException;
import exceptions.MessageTooBigForUDPException;
import exceptions.NotValidDomainNameException;
import exceptions.NotValidIPException;
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
	// private String resolverIP;
	private InetAddress ip;
	private int size;
	private Socket socket;
	private int messagesSent;
	private byte[] recieveReply;
	private boolean rrRecords;
	private TreeItem<String> root;
	private long startTime;
	private long stopTime;
	private TCPConnection tcp;
	private boolean closeConnection;
	private static final int MAX_MESSAGES_SENT = 3;
	private static final int TIME_OUT_MILLIS = 2000;
	public static final int MAX_UDP_SIZE = 1232;
	private static final String KEY_HEAD = "Head";
	private static final String KEY_QUERY = "Questions";
	private static final String KEY_REQUEST = "Request";
	private static final String KEY_LENGHT="Lenght";
	private static Logger LOGGER = Logger.getLogger(DomainConvert.class.getName());

	public MessageSender(boolean recursion, boolean dnssec, boolean rrRecords, String domain, Q_COUNT[] types,
			TRANSPORT_PROTOCOL transport_protocol, APPLICATION_PROTOCOL application_protocol, String resolverIP)
			throws NotValidIPException, UnsupportedEncodingException, NotValidDomainNameException,
			UnknownHostException {
		requests = new ArrayList<Request>();
		header = new Header(recursion, dnssec, types.length, rrRecords);
		size = Header.getSize();
		addRequests(types, checkAndStripFullyQualifyName(domain));
		// this.resolverIP = resolverIP;
		this.transport_protocol = transport_protocol;
		this.application_protocol = application_protocol;
		this.ip = InetAddress.getByName(resolverIP);
		this.recieveReply = new byte[1232];
		this.rrRecords = rrRecords;
		messagesSent = 0;
		tcp = null;
		closeConnection = true;
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
		if (rrRecords) {
			TreeItem<String> optRecord = new TreeItem<String>(MessageParser.KEY_ADDITIONAL_RECORDS);
			optRecord.getChildren().add(Response.getOptAsTreeItem());
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

	public void send()
			throws TimeoutException, IOException, MessageTooBigForUDPException, CouldNotUseHoldConnectionException {
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

	private void dnsOverUDP() throws TimeoutException, IOException, MessageTooBigForUDPException {
		if (size > MAX_UDP_SIZE)
			throw new MessageTooBigForUDPException();
		messagesSent = 0;
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

}
