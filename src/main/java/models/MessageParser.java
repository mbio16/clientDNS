package models;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.gson.GsonBuilder;
import exceptions.QueryIdNotMatchException;

public class MessageParser {
	private Header queryHeader;
	private Header header;
	private ArrayList<Request> qcountRequests;
	private ArrayList<Response> ancountResponses;
	private ArrayList<Response> nscountResponses;
	private ArrayList<Response> arcountResponses;
	private byte [] rawMessage;
	private int currentIndex; 
	private static final String KEY_HEAD="Head";
	private static final String KEY_QUESTIONS="Questions";
	private static final String KEY_ANSWERS="Answers";
	private static final String KEY_AUTHORITY="Authority";
	private static final String KEY_ADDITIONAL_RECORDS = "Aditional records";
	
	
	public MessageParser(byte [] rawMessage, Header queryHeader) {
	this.rawMessage = rawMessage;
	this.queryHeader = queryHeader;
	this.currentIndex = 0;
	this.qcountRequests = new ArrayList<Request>();
	this.ancountResponses = new ArrayList<Response>();
	this.nscountResponses = new ArrayList<Response>();
	this.arcountResponses = new ArrayList<Response>();
	}
	
	public void parse() throws QueryIdNotMatchException, UnknownHostException, UnsupportedEncodingException {
		header = new Header().parseHead(rawMessage);
		checkId();
		currentIndex += Header.getSize();
		for (int i = 0; i < header.getQdCount().intValue(); i++) {
			Request r = new  Request().parseRequest(rawMessage, currentIndex);
			qcountRequests.add(r);
			currentIndex += r.getSize();
		}
		
		for (int i = 0; i < header.getAnCount().intValue(); i++) {
			Response r = new Response().parseResponse(rawMessage, currentIndex);
			ancountResponses.add(r);
			currentIndex = r.getEndIndex() + 1;
		}
		for (int i = 0; i < header.getNsCount().intValue(); i++) {
			Response r = new Response().parseResponse(rawMessage, currentIndex);
			nscountResponses.add(r);
			currentIndex = r.getEndIndex() + 1;
		}
		for (int i = 0; i < header.getArCount().intValue(); i++) {
			Response r = new Response().parseResponse(rawMessage, currentIndex);
			arcountResponses.add(r);
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
				+ qcountRequests + ", ancountResponses=" + ancountResponses + ", nscountResponses=" + nscountResponses
				+ ", arcountResponses=" + arcountResponses + ", currentIndex=" + currentIndex + "]";
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject main = new JSONObject();
		JSONArray qc = new JSONArray();
		JSONArray an = new JSONArray();
		JSONArray ns = new JSONArray(); 
		JSONArray ar = new JSONArray();
		for (Request request : qcountRequests) {
			qc.add(request.getAsJson());
		}
		
		for (Response response: ancountResponses ) {
			an.add(response.getAsJson());
		}
		for (Response response : nscountResponses) {
			ns.add(response.getAsJson());
		}
		for (Response response: arcountResponses) {
			ar.add(response.getAsJson());
		}
		
		main.put(KEY_HEAD,header.getAsJson());
		main.put(KEY_QUESTIONS,qc);
		main.put(KEY_ANSWERS,an);
		main.put(KEY_AUTHORITY,ns);
		main.put(KEY_ADDITIONAL_RECORDS,ar);
		
		return main;
	}
	
	public String  getAsJsonString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(getAsJson());
		
		
	}
}
