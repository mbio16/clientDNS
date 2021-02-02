package models;

import java.net.UnknownHostException;
import java.util.ArrayList;

import exceptions.QueryIdNotMatchException;

public class MessageParser {
	private Header queryHeader;
	private Header header;
	private ArrayList<Request> qcountResponses;
	private ArrayList<Response> ancountResponses;
	private ArrayList<Response> nscountResponses;
	private ArrayList<Response> arcountResponses;
	private byte [] rawMessage;
	private int currentIndex; 
	
	
	public MessageParser(byte [] rawMessage, Header queryHeader) {
	this.rawMessage = rawMessage;
	this.queryHeader = queryHeader;
	this.currentIndex = 0;
	this.qcountResponses = new ArrayList<Request>();
	this.ancountResponses = new ArrayList<Response>();
	this.nscountResponses = new ArrayList<Response>();
	this.arcountResponses = new ArrayList<Response>();
	}
	
	public void parse() throws QueryIdNotMatchException, UnknownHostException {
		header = new Header().parseHead(rawMessage);
		checkId();
		currentIndex += Header.getSize();
		for (int i = 0; i < header.getQdCount().intValue(); i++) {
			Request r = new  Request().parseRequest(rawMessage, currentIndex);
			qcountResponses.add(r);
			currentIndex += r.getSize();
		}
		
		for (int i = 0; i < header.getAnCount().intValue(); i++) {
			Response r = new Response().parseResponse(rawMessage, currentIndex);
			ancountResponses.add(r);
			currentIndex = r.getEndIndex() + 1;
		}
	}
	
	
	private void checkId() throws QueryIdNotMatchException {
		if(!queryHeader.getId().equals(header.getId())) {
			throw new QueryIdNotMatchException();
		}
	}

	@Override
	public String toString() {
		return "MessageParser [queryHeader=" + queryHeader + ", header=" + header + ", qcountResponses="
				+ qcountResponses + ", ancountResponses=" + ancountResponses + ", nscountResponses=" + nscountResponses
				+ ", arcountResponses=" + arcountResponses + ", currentIndex=" + currentIndex + "]";
	}
	
	
}
