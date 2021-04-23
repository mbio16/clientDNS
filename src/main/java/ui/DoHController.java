package ui;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;

public class DoHController extends DNSController {
	
	public static final String FXML_FILE_NAME = "/fxml/DoH.fxml";
	
	@FXML
	private TextArea requestTextArea;
	@FXML
	private RadioButton jsonApiRadioButton;
	@FXML
	private RadioButton wireRadioButton;
	
	
	public DoHController() {
	super();
	
	}
	
	@Override
	public void initialize() {
		
	}
	
	public void setLabels() {
		
		
		setUserDataRecords();
	}
	
	public void loadDataFromSettings() {
		this.savedDomainNamesChoiseBox.getItems().setAll(settings.getDomainNamesDNS());
	}

}
