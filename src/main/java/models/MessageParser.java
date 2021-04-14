package models;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.gson.GsonBuilder;

import enums.APPLICATION_PROTOCOL;
import enums.TRANSPORT_PROTOCOL;
import exceptions.QueryIdNotMatchException;
import javafx.scene.control.TreeItem;

public class MessageParser {
	private Header queryHeader;
	private Header header;
	private ArrayList<Request> qcountRequests;
	private ArrayList<Response> ancountResponses;
	private ArrayList<Response> nscountResponses;
	private ArrayList<Response> arcountResponses;
	private byte[] rawMessage;
	private int currentIndex;
	private APPLICATION_PROTOCOL applicationProtocol;
	private static final String KEY_HEAD = "Head";
	private static final String KEY_QUESTIONS = "Questions";
	private static final String KEY_ANSWERS = "Answer";
	private static final String KEY_AUTHORITY = "Authority";
	public static final String KEY_ADDITIONAL_RECORDS = "Aditional records";
	private static final String KEY_LENGHT="Lenght";
	private TRANSPORT_PROTOCOL protocol;
	private TreeItem<String> main;
	private int byteSizeResponse;

	public MessageParser(byte[] rawMessage, Header queryHeader, TRANSPORT_PROTOCOL protocol) {
		this.rawMessage = rawMessage;
		this.queryHeader = queryHeader;
		this.currentIndex = 0;
		this.qcountRequests = new ArrayList<Request>();
		this.ancountResponses = new ArrayList<Response>();
		this.nscountResponses = new ArrayList<Response>();
		this.arcountResponses = new ArrayList<Response>();
		this.protocol = protocol;
		this.main = new TreeItem<String>(KEY_ANSWERS);
		byteSizeResponse = 0;
	}

	public void parse() throws QueryIdNotMatchException, UnknownHostException, UnsupportedEncodingException {
		applicationProtocol = APPLICATION_PROTOCOL.DNS;
		header = new Header().parseHead(rawMessage);
		checkId();
		currentIndex += Header.getSize();
		for (int i = 0; i < header.getQdCount().intValue(); i++) {
			Request r = new Request().parseRequest(rawMessage, currentIndex);
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

		if (protocol == TRANSPORT_PROTOCOL.TCP) {
			byteSizeResponse = currentIndex + 2;
		} else {
			byteSizeResponse = currentIndex;
		}

	}

	public void parseMDNS() throws QueryIdNotMatchException, UnknownHostException, UnsupportedEncodingException {
		applicationProtocol = APPLICATION_PROTOCOL.MDNS;
		header = new Header().parseHead(rawMessage);
		checkId();
		currentIndex += Header.getSize();
		for (int i = 0; i < header.getQdCount().intValue(); i++) {
			Request r = new Request().parseRequest(rawMessage, currentIndex);
			qcountRequests.add(r);
			currentIndex += r.getSize();
		}

		for (int i = 0; i < header.getAnCount().intValue(); i++) {
			Response r = new Response().parseResponseMDNS(rawMessage, currentIndex);
			ancountResponses.add(r);
			currentIndex = r.getEndIndex() + 1;
		}
		for (int i = 0; i < header.getNsCount().intValue(); i++) {
			Response r = new Response().parseResponseMDNS(rawMessage, currentIndex);
			nscountResponses.add(r);
			currentIndex = r.getEndIndex() + 1;
		}
		for (int i = 0; i < header.getArCount().intValue(); i++) {
			Response r = new Response().parseResponseMDNS(rawMessage, currentIndex);
			arcountResponses.add(r);
			currentIndex = r.getEndIndex() + 1;
		}
		byteSizeResponse = currentIndex;
	}
	public TreeItem<String> getAsTreeItem() {

		main.getChildren().add(header.getAsTreeItem());
		addRequestToTreeItem();
		addResponsToTreeItem(ancountResponses, KEY_ANSWERS);
		addResponsToTreeItem(nscountResponses, KEY_AUTHORITY);
		addResponsToTreeItem(arcountResponses, KEY_ADDITIONAL_RECORDS);
		if(protocol == TRANSPORT_PROTOCOL.TCP) {
			TreeItem<String> tcpTreeItem = new TreeItem<String>("");
			tcpTreeItem.getChildren().add(new TreeItem<String>(KEY_LENGHT + ": " + (byteSizeResponse -2 )));
			tcpTreeItem.getChildren().add(main);
			return tcpTreeItem;
			
		}
		return main;
	}

	private void addRequestToTreeItem() {
		TreeItem<String> questionTreeItem = new TreeItem<String>(KEY_QUESTIONS);
		if (header.getQdCount().getValue() > 0) {
			for (Request request : qcountRequests) {
				questionTreeItem.getChildren().add(request.getAsTreeItem());
			}
			main.getChildren().add(questionTreeItem);
		}

	}

	private void addResponsToTreeItem(ArrayList<Response> responses, String treeItemName) {
		TreeItem<String> item = new TreeItem<String>(treeItemName);
		if (responses.size() != 0) {
			for (Response response : responses) {
				item.getChildren().add(response.getAsTreeItem());
			}
			main.getChildren().add(item);
		}
	}

	private void checkId() throws QueryIdNotMatchException {
		if (!queryHeader.getId().equals(header.getId())) {
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

		for (Response response : ancountResponses) {
			an.add(response.getAsJson(applicationProtocol));
		}
		for (Response response : nscountResponses) {
			ns.add(response.getAsJson(applicationProtocol));
		}
		for (Response response : arcountResponses) {
			ar.add(response.getAsJson(applicationProtocol));
		}

		main.put(KEY_HEAD, header.getAsJson());
		main.put(KEY_QUESTIONS, qc);
		main.put(KEY_ANSWERS, an);
		main.put(KEY_AUTHORITY, ns);
		main.put(KEY_ADDITIONAL_RECORDS, ar);
		if (protocol == TRANSPORT_PROTOCOL.TCP) main.put(KEY_LENGHT, (byteSizeResponse -2));

		return main;
	}

	public String getAsJsonString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(getAsJson());
	}

	public int getByteSizeResponse() {
		return byteSizeResponse;
	}

}
