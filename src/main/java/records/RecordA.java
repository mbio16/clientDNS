package records;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.json.simple.JSONObject;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class RecordA extends Record {

	protected InetAddress ipAddress;
	protected String ipAddressAsString;
	private static final String KEY_ADDRESS="Ipv4";
	public RecordA(byte[] rawMessage, int lenght, int startIndex) throws UnknownHostException {
		super(rawMessage, lenght, startIndex);
		parseRecord();
	}
	
	private  void parseRecord() throws UnknownHostException {
		byte data [] = new byte [lenght];
		int j = 0;
		for (int i = startIndex; i < startIndex+lenght; i++) {
			data[j] = rawMessage[i];
			j++;
		}
		ipAddress = InetAddress.getByAddress(data);
		ipAddressAsString = ipAddress.getHostAddress();
	}

	@Override
	public String toString() {
		return KEY_ADDRESS +": " + ipAddressAsString;
	}
	
	@Override
	public String getStringToTreeView() {
		return "\t" +KEY_ADDRESS +": " + ipAddressAsString;
	}
	
	@Override
	public String getDataAsString() {
		
		return ipAddressAsString;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAsJson() {
		JSONObject object = new JSONObject();
		object.put(KEY_ADDRESS, ipAddressAsString);
		return object;
	}

	public TreeItem<TextFlow> getAsTreeItemFlow(){
		TextFlow flow = new TextFlow();
		Text key = new Text("Ipv4: ");
		Text value = new Text(ipAddressAsString);
		value.setStyle("fx-font-weight: bold");
		flow.getChildren().addAll(key,value);
		return new TreeItem<TextFlow>(flow);
		//main.getChildren().add(new TreeItem<String>(toString()));
	}
}
