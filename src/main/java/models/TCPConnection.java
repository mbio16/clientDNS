package models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import exceptions.CouldNotUseHoldConnectionException;
import exceptions.TimeoutException;

public class TCPConnection {
	private InetAddress destinationIp;
	private Socket socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private static final int DNS_PORT=53;
	private static final int SOCKET_TIME_OUT_SEC = 3;
	private byte [] responseMessage;
	public TCPConnection(InetAddress ip) {
		this.destinationIp = ip;
		responseMessage = null;
	}
	
	public byte [] send(byte messagesAsBytes [], InetAddress ip, boolean closeConnection) throws TimeoutException, IOException,CouldNotUseHoldConnectionException {
			if(socket == null) {
				connect();
			}
			if(!socket.getInetAddress().equals(ip)) {
				closeAll();
				this.destinationIp = ip;
				connect();
			}
		if(socket.isClosed() || !socket.isConnected()) {
			socket = null;
			connect();
		}
		sendAndRecieve(messagesAsBytes);
		if (closeConnection) closeAll();
		return responseMessage;
	}
	
	private void connect() throws IOException {
		InetSocketAddress socketAddress = new InetSocketAddress(destinationIp, DNS_PORT);
		socket = new Socket();
		socket.connect(socketAddress, SOCKET_TIME_OUT_SEC * 1000);
		outputStream = socket.getOutputStream();
		inputStream = socket.getInputStream();
	}
	public void closeAll() throws IOException {
		if(socket.isConnected() || !socket.isClosed()) {
		inputStream.close();
		outputStream.close();
		socket.close();
		}
	}
	
	private void sendAndRecieve(byte [] messagesAsBytes) throws CouldNotUseHoldConnectionException, TimeoutException, IOException {
		try {
		outputStream.write(messagesAsBytes);
		//dns message has first two bytes which is the lenght of the rest of the message
		byte [] sizeRicieve = inputStream.readNBytes(2);
		
		UInt16 messageSize = new UInt16().loadFromBytes(sizeRicieve[0],sizeRicieve[1]);
		
		//based on size get the dns message it self
		responseMessage= inputStream.readNBytes(messageSize.getValue());
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(e.toString());
			closeAll();
			throw new CouldNotUseHoldConnectionException();
		}
		catch (IOException e) {
			throw new TimeoutException();
		}
	}
}
