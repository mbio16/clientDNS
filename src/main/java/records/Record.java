package records;

import org.json.simple.JSONObject;

public class Record {
	protected int lenght;
	protected int startIndex;
	protected byte rawMessage[];

	public Record(byte[] rawMessage, int lenght, int startIndex) {
		this.lenght = lenght;
		this.rawMessage = rawMessage;
		this.startIndex = startIndex;
	}

	public JSONObject getAsJson() {
		return new JSONObject();
	}

	public String[] getValesForTreeItem() {
		return null;
	}

	public String getStringToTreeView() {
		return null;
	}

	public int getLenght() {
		return lenght;
	}

	public String getDataForTreeViewName() {
		return "";
	}

	public int getStartIndex() {
		return startIndex;
	}

	public byte[] getRawMessage() {
		return rawMessage;
	}

	// Has to be overided in the children
	public String getDataAsString() {
		return null;
	}

	protected byte[] get4bytes(int currentIndex) {
		byte[] ttlBytes = { rawMessage[currentIndex], rawMessage[currentIndex + 1], rawMessage[currentIndex + 2],
				rawMessage[currentIndex + 3] };
		return ttlBytes;
	}

}
