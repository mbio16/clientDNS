package ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Logger;

import application.Main;
import enums.IP_PROTOCOL;
import enums.Q_COUNT;
import enums.RESPONSE_MDNS_TYPE;
import exceptions.CouldNotUseHoldConnectionException;
import exceptions.MessageTooBigForUDPException;
import exceptions.MoreRecordsTypesWithPTRException;
import exceptions.NonRecordSelectedException;
import exceptions.NotValidDomainNameException;
import exceptions.NotValidIPException;
import exceptions.QueryIdNotMatchException;
import exceptions.TimeoutException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.DomainConvert;
import models.Ip;
import models.Language;
import models.MessageParser;
import models.MessageSender;

public class MDNSController extends GeneralController {

	public static final String FXML_FILE_NAME = "/fxml/MDNS.fxml";

	// menu items
	@FXML
	protected Menu actionMenu;
	@FXML
	protected Menu historyMenu;
	@FXML
	protected Menu languageMenu;

	@FXML
	protected MenuItem backMenuItem;
	@FXML 
	private MenuItem deleteMDNSDomainNameHistory;
	@FXML
	protected RadioMenuItem czechRadioButton;
	@FXML
	protected RadioMenuItem englishRadioButton;
	@FXML 
	private MenuItem mdnsMenuItem;
	@FXML 
	private MenuItem iPv4MulticastMenuItem;
	@FXML 
	private MenuItem iPv6MulticastMenuItem;
	@FXML
	protected Button copyRequestJsonButton;
	@FXML
	protected Button copyResponseJsonButton;
	// butons
	@FXML
	protected Button sendButton;

	// text fields
	@FXML
	protected TextField domainNameTextField;

	// radio buttons
	@FXML
	private RadioButton ipv4RadioButton;
	@FXML
	private RadioButton ipv6RadioButton;
	@FXML
	private RadioButton multicastResponseRadioButton;
	@FXML 
	private RadioButton unicastResponseRadioButton;

	// checkboxes
	@FXML
	protected CheckBox aCheckBox;
	@FXML
	protected CheckBox aaaaCheckBox;
	@FXML
	protected CheckBox nsecCheckBox;
	@FXML
	private CheckBox srvCheckBox;
	@FXML
	protected CheckBox ptrCheckBox;
	@FXML
	protected CheckBox txtCheckBox;
	@FXML
	protected CheckBox dnssecRecordsRequestCheckBox;
	@FXML
	protected CheckBox anyCheckBox;
	// titledpane

	@FXML
	protected TitledPane domainNameTitledPane;
	@FXML
	protected TitledPane ipTitledPane;
	@FXML
	protected TitledPane dnssecTitledPane;
	@FXML
	protected TitledPane recordTypeTitledPane;
	@FXML
	protected TitledPane queryTitledPane;
	@FXML
	protected TitledPane responseTitledPane;
	@FXML 
	private TitledPane multicastResponseTitledPane;


	// labels
	@FXML
	protected Label responseTimeLabel;
	@FXML
	protected Label responseTimeValueLabel;
	@FXML
	protected Label numberOfMessagesLabel;
	@FXML
	protected Label numberOfMessagesValueLabel;

	// toogleGroup
	private ToggleGroup ipToggleGroup;

	private ToggleGroup multicastResponseToggleGroup; 
	@FXML
	protected ComboBox<String> savedDomainNamesChoiseBox;

	@FXML
	protected TreeView<String> requestTreeView;
	@FXML
	protected TreeView<String> responseTreeView;

	protected MessageParser parser;
	protected MessageSender sender;

	public MDNSController() {
		super();
		LOGGER = Logger.getLogger(MDNSController.class.getName());
	}

	public void initialize() {
		ipToggleGroup = new ToggleGroup();
		ipv4RadioButton.setToggleGroup(ipToggleGroup);
		ipv6RadioButton.setToggleGroup(ipToggleGroup);

		multicastResponseToggleGroup = new ToggleGroup();
		multicastResponseRadioButton.setToggleGroup(multicastResponseToggleGroup);
		unicastResponseRadioButton.setToggleGroup(multicastResponseToggleGroup);

	}


	public void setLabels() {
		// define group to iterate over it

		Label[] labelsArray = new Label[] { responseTimeLabel, numberOfMessagesLabel };
		TitledPane[] titlePaneArray = new TitledPane[] {
				domainNameTitledPane,
				ipTitledPane,
				multicastResponseTitledPane,
				queryTitledPane,
				responseTitledPane
				};
		dnssecRecordsRequestCheckBox.setText(language.getLanguageBundle().getString(dnssecRecordsRequestCheckBox.getId()));
		// set labels to current language in menu
		
		actionMenu.setText(language.getLanguageBundle().getString(actionMenu.getId()));
		languageMenu.setText(language.getLanguageBundle().getString(languageMenu.getId()));
		historyMenu.setText(language.getLanguageBundle().getString(historyMenu.getId()));
		backMenuItem.setText(language.getLanguageBundle().getString(backMenuItem.getId()));
		deleteMDNSDomainNameHistory.setText(language.getLanguageBundle().getString(deleteMDNSDomainNameHistory.getId()));
		for (TitledPane titledPane : titlePaneArray) {
			titledPane.setText(language.getLanguageBundle().getString(titledPane.getId()));
		}

		for (Label label : labelsArray) {
			label.setText(language.getLanguageBundle().getString(label.getId()));
		}

		// set sendButton
		sendButton.setText(language.getLanguageBundle().getString(sendButton.getId()));
		copyResponseJsonButton.setText(language.getLanguageBundle().getString(copyResponseJsonButton.getId()));
		copyRequestJsonButton.setText(language.getLanguageBundle().getString(copyRequestJsonButton.getId()));
		if (language.getCurrentLanguage().equals(Language.CZECH)) {
			czechRadioButton.setSelected(true);
			englishRadioButton.setSelected(false);
		} else {
			czechRadioButton.setSelected(false);
			englishRadioButton.setSelected(true);
		}
		savedDomainNamesChoiseBox.getItems().addAll(settings.getDomainNamesMDNS());
		
		setUserDataRecords();
		setIpUserData();
		setMDNSType();
		setUserDataWireshark();
	}
	private void setUserDataWireshark() {
		mdnsMenuItem.setUserData("udp.port == 5353");
		iPv4MulticastMenuItem.setUserData("ip.addr == 224.0.0.251 && udp.port == 5353");
		iPv6MulticastMenuItem.setUserData("ipv6.addr == ff02::fb && udp.port == 5353");
	}
	private void setMDNSType() {
		multicastResponseRadioButton.setUserData(RESPONSE_MDNS_TYPE.RESPONSE_MULTICAST);
		unicastResponseRadioButton.setUserData(RESPONSE_MDNS_TYPE.RESPONSE_UNICAST);
	}
	private void setIpUserData() {
		ipv4RadioButton.setUserData(IP_PROTOCOL.IPv4);
		ipv6RadioButton.setUserData(IP_PROTOCOL.IPv6);
	}
	
	private void setUserDataRecords() {
		aCheckBox.setUserData(Q_COUNT.A);
		aaaaCheckBox.setUserData(Q_COUNT.AAAA);
		ptrCheckBox.setUserData(Q_COUNT.PTR);
		nsecCheckBox.setUserData(Q_COUNT.NSEC);
		txtCheckBox.setUserData(Q_COUNT.TXT);
		srvCheckBox.setUserData(Q_COUNT.SRV);
		anyCheckBox.setUserData(Q_COUNT.ANY);
	}
	
	private Q_COUNT [] getRecordTypes()  throws MoreRecordsTypesWithPTRException, NonRecordSelectedException {
		ArrayList<Q_COUNT> list = new ArrayList<Q_COUNT>();
		CheckBox[] checkBoxArray = { 
				aCheckBox,
				aaaaCheckBox,
				ptrCheckBox,
				txtCheckBox,
				nsecCheckBox,
				srvCheckBox,
				anyCheckBox 
				};
		for (int i = 0; i < checkBoxArray.length; i++) {
			if (checkBoxArray[i].isSelected()) {
				list.add((Q_COUNT) checkBoxArray[i].getUserData());
			}
		}
		if (list.contains(Q_COUNT.PTR) && list.size() > 1) {
			throw new MoreRecordsTypesWithPTRException();
		}
		if (list.size() == 0) {
			throw new NonRecordSelectedException();
		}
		Q_COUNT returnList[] = new Q_COUNT[list.size()];
		for (int i = 0; i < returnList.length; i++) {
			returnList[i] = list.get(i);
		}
		return returnList;
	}
	
	private String getDomain() throws UnsupportedEncodingException, NotValidDomainNameException {
		String domain = domainNameTextField.getText();
		
		if(ptrCheckBox.isSelected()) {
			if(Ip.isIpValid(domain) || domain.contains(".arpa")) {
				return domain;
			}
			else {
				throw new NotValidDomainNameException();
			}
		}
		if (DomainConvert.isValidDomainName(domain)) {
			return domain;
		}

			DomainConvert.encodeMDNS(domain);
			return domain;
	}
	@FXML
	private void getWiresharkFilter(ActionEvent event) {
		MenuItem item = (MenuItem) event.getSource();
		copyDataToClipBoard((String) item.getUserData());
	}
	
	@FXML
	private void onDomainNameMDNSChoiseBoxFired(Event event) {
		savedDomainNamesChoiseBox.getItems().removeAll(savedDomainNamesChoiseBox.getItems());
		savedDomainNamesChoiseBox.getItems().addAll(settings.getDomainNamesMDNS());
	}
	
	@FXML
	private void onDomainNameMDNSChoiseBoxAction(Event event) {
		try {
			if (!savedDomainNamesChoiseBox.getValue().equals(null)
					&& !savedDomainNamesChoiseBox.getValue().equals("")) {
				domainNameTextField.setText(savedDomainNamesChoiseBox.getValue());
			}
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		}
	}
	protected void setDisableJSonButtons(boolean disable) {
		copyRequestJsonButton.setDisable(disable);
		copyResponseJsonButton.setDisable(disable);
	}

	@FXML
	protected void czechSelected(ActionEvent event) {
		language.changeLanguageBundle(true);
		setLabels();
	}

	@FXML
	protected void englishSelected(ActionEvent event) {
		language.changeLanguageBundle(false);
		setLabels();
	}

	@FXML
	protected void backButtonFirred(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(MainController.FXML_FILE_NAME));
			Stage newStage = new Stage();
			newStage.setScene(new Scene((Parent) loader.load()));
			newStage.setTitle(APP_TITTLE);
			GeneralController controller = (GeneralController) loader.getController();
			controller.setLanguage(language);
			controller.setSettings(settings);
			newStage.initModality(Modality.APPLICATION_MODAL);
			
			Stage oldStage = (Stage) sendButton.getScene().getWindow();
			newStage.setX(oldStage.getX() + (oldStage.getWidth()/4));
			newStage.setY(oldStage.getY() + (oldStage.getHeight()/4));
			newStage.getIcons().add(new Image(Main.ICON_URI));
			newStage.show();
			
			oldStage.close();
			controller.setLabels();
			controller.setIpDns(ipDns);
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("windowError"));
			alert.showAndWait();
		}
	}

	private void logAction(Q_COUNT [] records, String domain, boolean dnssec, IP_PROTOCOL networkProtocol,RESPONSE_MDNS_TYPE mdnsType) {
		String res = "";
		res += "Domain: " + domain + "\n";
		res += "DNSSEC: " + dnssec + "\n";
		res += "IP: " + networkProtocol.toString() + "\n";
		res += "MDNS response: " + mdnsType.toString() + "\n";
		res += "Records: \n";
		
		for (Q_COUNT q_COUNT : records) {
			res += "\t" + q_COUNT.toString() + "\n";
		}
		LOGGER.info(res);
		
	}
	protected void showAller(String exceptionName) {
		Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString(exceptionName));
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner((Stage) sendButton.getScene().getWindow());
		alert.show();
	}
	
	@FXML
	protected void sendButtonFired(ActionEvent event) {
		try {
		Q_COUNT records [] = getRecordTypes();
		String domain = getDomain();
		boolean dnssec = dnssecRecordsRequestCheckBox.isSelected();
		IP_PROTOCOL networkProtocol = (IP_PROTOCOL) ipToggleGroup.getSelectedToggle().getUserData();
		RESPONSE_MDNS_TYPE mdnsType = (RESPONSE_MDNS_TYPE) multicastResponseToggleGroup.getSelectedToggle().getUserData();
		logAction(records, domain, dnssec, networkProtocol, mdnsType);
		sender = new MessageSender(
				dnssec,
				domain,
				records,
				networkProtocol,
				mdnsType);
		sender.send();
		parser = new MessageParser(sender.getRecieveReply(), sender.getHeader(), null);
		parser.parseMDNS();
		settings.addMDNSDomain(domain);
		setControls();
		}
		catch (NotValidDomainNameException | NotValidIPException
				| MoreRecordsTypesWithPTRException | NonRecordSelectedException | TimeoutException | IOException
				| QueryIdNotMatchException | MessageTooBigForUDPException | CouldNotUseHoldConnectionException e) {
			String fullClassName = e.getClass().getSimpleName();
			LOGGER.info(fullClassName);
			if (sender != null)
				numberOfMessagesValueLabel.setText("" + sender.getMessageSent());
			showAller(fullClassName);
		} catch (Exception e) {
			LOGGER.warning(e.toString());
			showAller("Exception");
		}
		}
	
	@FXML
	protected void onDomainNameAction(ActionEvent e) {
		sendButtonFired(e);
	}
	protected void setControls() {
		responseTreeView.setRoot(parser.getAsTreeItem());
		requestTreeView.setRoot(sender.getAsTreeItem());
		responseTimeValueLabel.setText("" + sender.getTimeElapsed());
		numberOfMessagesValueLabel.setText("" + sender.getMessageSent());
		setDisableJSonButtons(false);
		responseTreeView.getTreeItem(0).setExpanded(true);
		expandAll(requestTreeView);
		expandAll(responseTreeView);
		queryTitledPane.setText(language.getLanguageBundle().getString(queryTitledPane.getId().toString()) + " ("
				+ sender.getByteSizeQuery() + " B)");
		responseTitledPane.setText(language.getLanguageBundle().getString(responseTitledPane.getId().toString()) + " ("
				+ parser.getByteSizeResponse() + " B)");
	}
	protected void copyDataToClipBoard(String data) {
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
		content.putString(data);
		clipboard.setContent(content);
	}

	@FXML
	protected void copyJsonRequestDataFired(ActionEvent event) {
		copyDataToClipBoard(sender.getAsJsonString());

	}

	@FXML
	protected void copyJsonResponseDataFired(ActionEvent event) {
		copyDataToClipBoard(parser.getAsJsonString());
	}

	protected ArrayList<String> autobindingsStringsArray(String textToFind, ArrayList<String> arrayToCompare) {
		ArrayList<String> result = new ArrayList<String>();
		for (String string : arrayToCompare) {
			if (string.contains(textToFind))
				result.add(string);
		}

		return result;
	}

	protected void autobinging(String textFromField, ArrayList<String> fullArray, ComboBox<String> box) {
		ArrayList<String> result = autobindingsStringsArray(textFromField, fullArray);
		if (result.size() == 0) {
			box.hide();
			box.getItems().removeAll(box.getItems());
			box.getItems().addAll(settings.getDomainNamesDNS());
		} else {
			box.getItems().removeAll(savedDomainNamesChoiseBox.getItems());
			box.getItems().setAll(result);
			box.show();
		}
	}

	@FXML
	private void deleteMDNSDomainNameHistoryFired(Event event) {
		settings.eraseMDNSDomainNames();
		savedDomainNamesChoiseBox.getItems().removeAll(savedDomainNamesChoiseBox.getItems());
	}
	@FXML
	protected void expandAllRequestOnClick(Event event) {
		expandAll(requestTreeView);
	}

	@FXML
	protected void expandAllResponseOnClick(Event event) {
		expandAll(responseTreeView);
	}

	@FXML
	private void domainNameKeyPressed(KeyEvent event) {
		controlKeys(event, domainNameTextField);
		autobinging(domainNameTextField.getText(), settings.getDomainNamesMDNS(), savedDomainNamesChoiseBox);
	}
	@FXML
	private void treeViewclicked(Event event) {
		
	}
	
	protected void controlKeys(KeyEvent e, TextField text) {
		byte b = e.getCharacter().getBytes()[0];
		if (b == (byte) 0x08 && text.getText().length() >= 1 && isRightToLeft(text.getText())) {
			System.out.println(text.getText());
			text.setText(text.getText().substring(1, text.getText().length()));
		}
	}
	private boolean isRightToLeft(String text) {
		char[] chars = text.toCharArray();
		for(char c: chars){
		    if(c >= 0x500 && c <= 0x6ff){
		        return true;
		        		     }
		}
		return false;
	}
	protected void expandAll(TreeView<String> t) {
		try {
			int i = 0;
			while (true) {
				if (t.getTreeItem(i).getValue() == null) {
					break;
				} else {
					t.getTreeItem(i).setExpanded(true);
				}
				i++;
			}
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		}
	}
}
