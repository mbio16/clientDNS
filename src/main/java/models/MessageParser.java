package models;

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
	
	public void parse() throws QueryIdNotMatchException {
		header = new Header().parseHead(rawMessage);
		checkId();
		currentIndex += Header.getSize();
		for (int i = 0; i < header.getQdCount().intValue(); i++) {
			Request r = new  Request().parseRequest(rawMessage, currentIndex);
			qcountResponses.add(r);
			currentIndex += r.getSize();
		}
		
		for (int i = 0; i < header.getAnCount().intValue(); i++) {
			Response r = new Response(rawMessage);
			r.parseResponse(currentIndex);
		}
		System.out.println(currentIndex);
		System.out.println(header.toString());
		System.out.println(qcountResponses.toString());
	}
	private void checkId() throws QueryIdNotMatchException {
		if(!queryHeader.getId().equals(header.getId())) {
			throw new QueryIdNotMatchException();
		}
	}
}
