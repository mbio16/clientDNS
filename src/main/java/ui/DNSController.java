package ui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;
import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import exceptions.CouldNotUseHoldConnectionException;
import exceptions.DnsServerIpIsNotValidException;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import models.DomainConvert;
import models.Ip;
import models.Language;
import models.MessageParser;
import models.MessageSender;
import models.TCPConnection;

public class DNSController extends MDNSController {

	public static final String FXML_FILE_NAME = "/fxml/DNS.fxml";

	// text fields
	@FXML
	private TextField dnsServerTextField;

	// radio buttons

	@FXML
	private RadioButton tcpRadioButton;
	@FXML
	private RadioButton udpRadioButton;
	@FXML
	private RadioButton recursiveQueryRadioButton;
	@FXML
	private RadioButton iterativeQueryRadioButton;
	@FXML
	private RadioButton cloudflareIpv4RadioButton;
	@FXML
	private RadioButton googleIpv4RadioButton;
	@FXML
	private RadioButton cznicIpv4RadioButton;
	@FXML
	private RadioButton cloudflareIpv6RadioButton;
	@FXML
	private RadioButton googleIpv6RadioButton;
	@FXML
	private RadioButton cznicIpv6RadioButton;
	@FXML
	private RadioButton systemIpv4DNSRadioButton;
	@FXML
	private RadioButton systemIpv6DNSRadioButton;
	@FXML
	private RadioButton customDNSRadioButton;

	// menu items
	@FXML
	private MenuItem deleteDomainNameHistory;
	@FXML
	private MenuItem deleteDNSServersHistory;

	// checkboxes
	@FXML
	private CheckBox soaCheckBox;
	@FXML
	private CheckBox dnskeyCheckBox;
	@FXML
	private CheckBox dsCheckBox;
	@FXML
	private CheckBox caaCheckBox;
	@FXML
	private CheckBox txtCheckBox;
	@FXML
	private CheckBox rrsigCheckBox;
	@FXML
	private CheckBox holdConectionCheckbox;
	@FXML
	private CheckBox nsecCheckBox;

	@FXML
	private CheckBox nsec3CheckBox;

	// titledpane
	@FXML
	private TitledPane transportTitledPane;
	@FXML
	private TitledPane dnsServerTitledPane;
	@FXML
	private TitledPane iterativeTitledPane;

	// toogleGroup
	private ToggleGroup transportToggleGroup;
	private ToggleGroup iterativeToggleGroup;
	private ToggleGroup dnsserverToggleGroup;
	// choice box
	@FXML
	private ChoiceBox<String> savedDNSChoiceBox;
	@FXML
	private ImageView cloudflareIpv4ImageView;
	@FXML
	private ImageView googleIpv4IamgeView;
	@FXML
	private ImageView cznicIpv4RadioIamgeView;
	@FXML
	private ImageView cloudflareIpv6ImageView;
	@FXML
	private ImageView googleIpv6ImagaView;
	@FXML
	private ImageView cznicIpv6ImageView;
	@FXML
	private ImageView systemIpv4DNSImageView;
	@FXML
	private ImageView systemIpv6DNSIamgeView;
	@FXML
	private ImageView custumImageView;

	private TCPConnection tcpConnection;
	public DNSController() {

		super();
		LOGGER = Logger.getLogger(DNSController.class.getName());

	}

	public void initialize() {
		transportToggleGroup = new ToggleGroup();
		tcpRadioButton.setToggleGroup(transportToggleGroup);
		udpRadioButton.setToggleGroup(transportToggleGroup);

		iterativeToggleGroup = new ToggleGroup();
		iterativeQueryRadioButton.setToggleGroup(iterativeToggleGroup);
		recursiveQueryRadioButton.setToggleGroup(iterativeToggleGroup);

		dnssecToggleGroup = new ToggleGroup();

		dnssecYesRadioButton.setToggleGroup(dnssecToggleGroup);
		dnssecNoRadioButton.setToggleGroup(dnssecToggleGroup);

		dnsserverToggleGroup = new ToggleGroup();
		cloudflareIpv4RadioButton.setToggleGroup(dnsserverToggleGroup);
		cloudflareIpv6RadioButton.setToggleGroup(dnsserverToggleGroup);
		googleIpv4RadioButton.setToggleGroup(dnsserverToggleGroup);
		googleIpv6RadioButton.setToggleGroup(dnsserverToggleGroup);
		cznicIpv4RadioButton.setToggleGroup(dnsserverToggleGroup);
		cznicIpv6RadioButton.setToggleGroup(dnsserverToggleGroup);
		systemIpv4DNSRadioButton.setToggleGroup(dnsserverToggleGroup);
		systemIpv6DNSRadioButton.setToggleGroup(dnsserverToggleGroup);
		customDNSRadioButton.setToggleGroup(dnsserverToggleGroup);
	}

	public void copyImageViewFired(MouseEvent event) {
		if (event.getSource() == custumImageView) {
			copyDataToClipBoard(dnsServerTextField.getText());
		} else {
			System.out.println("ano");
			ImageView v = (ImageView) event.getSource();
			System.out.println(v.getUserData());
			copyDataToClipBoard(v.getUserData().toString());
		}
	}

	private void setSystemDNS() {
		if (ipDns.getIpv4DnsServer().equals("")) {
			systemIpv4DNSRadioButton.setSelected(false);
			systemIpv4DNSRadioButton.setText(language.getLanguageBundle().getString("ipv4SystemDNSIsNotEnabled"));
			systemIpv4DNSRadioButton.setDisable(true);
			systemIpv4DNSImageView.setDisable(true);
			;

		} else {
			systemIpv4DNSRadioButton.setText(ipDns.getIpv4DnsServer());
			systemIpv4DNSRadioButton.setUserData(ipDns.getIpv4DnsServer());
			systemIpv4DNSImageView.setDisable(false);
			systemIpv4DNSImageView.setUserData(ipDns.getIpv4DnsServer());
		}
		if (ipDns.getIpv6DnsServer().equals("")) {
			systemIpv6DNSRadioButton.setSelected(false);
			systemIpv6DNSRadioButton.setText(language.getLanguageBundle().getString("ipv6SystemDNSIsNotEnabled"));
			systemIpv6DNSRadioButton.setDisable(true);
			systemIpv6DNSIamgeView.setDisable(true);
			systemIpv6DNSIamgeView.setUserData(ipDns.getIpv6DnsServer());
		} else {
			systemIpv6DNSRadioButton.setText(ipDns.getIpv6DnsServer());
			systemIpv6DNSRadioButton.setUserData(ipDns.getIpv6DnsServer());
			systemIpv6DNSIamgeView.setDisable(false);
		}
	}

	public void setLabels() {
		// define group to iterate over it
		TitledPane titlePaneArray[] = new TitledPane[] { domainNameTitledPane, transportTitledPane, dnssecTitledPane,
				recordTypeTitledPane, dnsServerTitledPane, iterativeTitledPane, responseTitledPane, queryTitledPane };

		// same for radio buttons
		RadioButton[] radioButtonArray = new RadioButton[] { dnssecYesRadioButton, dnssecNoRadioButton,
				iterativeQueryRadioButton, recursiveQueryRadioButton };

		Label[] labelsArray = new Label[] { responseTimeLabel, numberOfMessagesLabel };

		// set labels to current language in menu
		backMenuItem.setText(language.getLanguageBundle().getString(backMenuItem.getId()));
		actionMenu.setText(language.getLanguageBundle().getString(actionMenu.getId()));
		languageMenu.setText(language.getLanguageBundle().getString(languageMenu.getId()));
		for (TitledPane titledPane : titlePaneArray) {
			titledPane.setText(language.getLanguageBundle().getString(titledPane.getId()));
		}

		for (RadioButton radioButton : radioButtonArray) {
			radioButton.setText(language.getLanguageBundle().getString(radioButton.getId()));
		}

		for (Label label : labelsArray) {
			label.setText(language.getLanguageBundle().getString(label.getId()));
		}

		// set sendButton
		sendButton.setText(language.getLanguageBundle().getString(sendButton.getId()));

		if (language.getCurrentLanguage().equals(Language.CZECH)) {
			czechRadioButton.setSelected(true);
			englishRadioButton.setSelected(false);
		} else {
			czechRadioButton.setSelected(false);
			englishRadioButton.setSelected(true);
		}

		dnssecRecordsRequestCheckBox
				.setText(language.getLanguageBundle().getString(dnssecRecordsRequestCheckBox.getId()));

		// set system dns
		setSystemDNS();
		// setUserData

		setUserDataDnsServers();
		setUserDataRecords();
		setUserDataTransportProtocol();
		// permform radio buttons actions
		onRadioButtonChange(null);

		responseTreeView.setStyle("-fx-font-size: 12");
		requestTreeView.setStyle("-fx-font-size: 12");

		copyRequestJsonButton.setText(language.getLanguageBundle().getString(copyRequestJsonButton.getId()));
		copyResponseJsonButton.setText(language.getLanguageBundle().getString(copyResponseJsonButton.getId()));
		deleteDomainNameHistory.setText(language.getLanguageBundle().getString(deleteDomainNameHistory.getId()));
		deleteDNSServersHistory.setText(language.getLanguageBundle().getString(deleteDNSServersHistory.getId()));
	}

	private void setUserDataTransportProtocol() {
		tcpRadioButton.setUserData(TRANSPORT_PROTOCOL.TCP);
		udpRadioButton.setUserData(TRANSPORT_PROTOCOL.UDP);
	}

	private void setUserDataDnsServers() {
		cloudflareIpv4RadioButton.setUserData("1.1.1.1");
		googleIpv4RadioButton.setUserData("8.8.8.8");
		cznicIpv4RadioButton.setUserData("193.17.47.1");
		cloudflareIpv6RadioButton.setUserData("2606:4700:4700::1111");
		googleIpv6RadioButton.setUserData("2001:4860:4860::8888");
		cznicIpv6RadioButton.setUserData("2001:148f:ffff::1");
		cloudflareIpv4ImageView.setUserData("1.1.1.1");
		googleIpv4IamgeView.setUserData("8.8.8.8");
		;
		cznicIpv4RadioIamgeView.setUserData("193.17.47.1");
		;
		cloudflareIpv6ImageView.setUserData("2606:4700:4700::1111");
		googleIpv6ImagaView.setUserData("2001:4860:4860::8888");
		cznicIpv6ImageView.setUserData("2001:148f:ffff::1");

	}

	private void setUserDataRecords() {
		aCheckBox.setUserData(Q_COUNT.A);
		aaaaCheckBox.setUserData(Q_COUNT.AAAA);
		cnameCheckBox.setUserData(Q_COUNT.CNAME);
		mxCheckBox.setUserData(Q_COUNT.MX);
		nsCheckBox.setUserData(Q_COUNT.NS);
		caaCheckBox.setUserData(Q_COUNT.CAA);
		ptrCheckBox.setUserData(Q_COUNT.PTR);
		txtCheckBox.setUserData(Q_COUNT.TXT);
		dnskeyCheckBox.setUserData(Q_COUNT.DNSKEY);
		soaCheckBox.setUserData(Q_COUNT.SOA);
		dsCheckBox.setUserData(Q_COUNT.DS);
		rrsigCheckBox.setUserData(Q_COUNT.RRSIG);
		nsecCheckBox.setUserData(Q_COUNT.NSEC);
		nsec3CheckBox.setUserData(Q_COUNT.NSEC3);

	}

	public void loadDataFromSettings() {
		savedDomainNamesChoiseBox.getItems().addAll(settings.getDomainNamesDNS());
		savedDNSChoiceBox.getItems().addAll(settings.getDnsServers());
	}

	@FXML
	public void backButtonFirred(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(MainController.FXML_FILE_NAME));
			Stage newStage = new Stage();
			newStage.setScene(new Scene((Parent) loader.load()));
			GeneralController controller = (GeneralController) loader.getController();
			controller.setLanguage(language);
			controller.setSettings(settings);
			newStage.setTitle(APP_TITTLE);
			newStage.show();
			Stage mainStage = (Stage) sendButton.getScene().getWindow();
			mainStage.close();
			controller.setLabels();
			controller.setIpDns(ipDns);
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("windowError"));
			alert.showAndWait();
		}
	}

	@FXML
	public void onRadioButtonChange(ActionEvent event) {
		if (dnsserverToggleGroup.getSelectedToggle().getUserData() != null) {
			dnsServerTextField.setText("");
			copyDataToClipBoard(dnsserverToggleGroup.getSelectedToggle().getUserData().toString());
		}

	}

	private String getDnsServerIp() throws DnsServerIpIsNotValidException, UnknownHostException {
		if (DomainConvert.isValidDomainName(dnsServerTextField.getText()) && customDNSRadioButton.isSelected()) {
			InetAddress ipaddress = InetAddress.getByName(dnsServerTextField.getText());
			System.out.println("IP address: " + ipaddress.getHostAddress());
			String ipAddr = ipaddress.getHostAddress().toString();
			Alert info = new Alert(Alert.AlertType.INFORMATION);
			info.setTitle(language.getLanguageBundle().getString("translated"));
			info.setContentText(dnsServerTextField.getText() + " "
					+ language.getLanguageBundle().getString("rootServerWasTranslated") + ipAddr);
			info.show();
			dnsServerTextField.setText(ipAddr);
			settings.addDNSServer(ipAddr);
			return ipAddr;
		}

		if ((!dnsServerTextField.getText().equals("")) && customDNSRadioButton.isSelected()) {
			if (Ip.isIpValid(dnsServerTextField.getText())) {
				System.out.println(dnsServerTextField);
				settings.addDNSServer(dnsServerTextField.getText());
				return dnsServerTextField.getText();
			} else {
				throw new DnsServerIpIsNotValidException();
			}
		} else {
			return dnsserverToggleGroup.getSelectedToggle().getUserData().toString();
		}

	}

	private String getDomain() throws NotValidDomainNameException {
		try {
			String domain = (domainNameTextField.getText());
			LOGGER.info("Domain name: " + domain);
			if ((Ip.isIPv4Address(domain) || Ip.isIpv6Address(domain)) && ptrCheckBox.isSelected()) {
				LOGGER.info("PTR record request");
				return domain;
			}
			if (DomainConvert.isValidDomainName(domain)) {
				settings.addDNSDomain(domain);

				return DomainConvert.encodeIDN(domain);
			} else {
				throw new NotValidDomainNameException();
			}
		} catch (Exception e) {
			LOGGER.warning(e.toString());
			throw new NotValidDomainNameException();
		}
	}

	private Q_COUNT[] getRecordTypes() throws MoreRecordsTypesWithPTRException, NonRecordSelectedException {
		ArrayList<Q_COUNT> list = new ArrayList<Q_COUNT>();
		CheckBox[] checkBoxArray = { aCheckBox, aaaaCheckBox, nsCheckBox, mxCheckBox, soaCheckBox, cnameCheckBox,
				ptrCheckBox, dnskeyCheckBox, dsCheckBox, caaCheckBox, txtCheckBox, rrsigCheckBox, nsecCheckBox,
				nsec3CheckBox };
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

	private TRANSPORT_PROTOCOL getTransportProtocol() {
		if (udpRadioButton.isSelected()) {
			return TRANSPORT_PROTOCOL.UDP;
		} else {
			return TRANSPORT_PROTOCOL.TCP;
		}
	}
	
	private void closeHoldedConnection(){
		try {
		if (tcpConnection != null) tcpConnection.closeAll();
		}
		catch (Exception e) {
			LOGGER.warning("Can not close connection to object null");
		}
	}
	
	private boolean isRecursiveSet() {
		return recursiveQueryRadioButton.isSelected();
	}

	private boolean isDNSSECSet() {
		return dnssecYesRadioButton.isSelected();
	}

	private void logMessage(String dnsServer, String domain, Q_COUNT[] records, boolean recursive, boolean dnssec,
			TRANSPORT_PROTOCOL transport, boolean dnssecRRsig, boolean holdConnection) {
		LOGGER.info("DNS server: " + dnsServer + "\n" + "Domain: " + domain + "\n" + "Records: " + records.toString()
				+ "\n" + "Recursive:" + recursive + "\n" + "DNSSEC: " + dnssec + "\n" + "DNSSEC sig records"
				+ dnssecRRsig + "\n" + "Transport protocol: " + transport + "\n"+ "Hold connection: "+ holdConnection + "\n" + "Application protocol: "
				+ APPLICATION_PROTOCOL.DNS);
	}

	private void setControls() {
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

	private void showAller(String exceptionName) {
		Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString(exceptionName));
		alert.show();
	}

	@FXML
	public void sendButtonFired(ActionEvent event) {
		try {
			String dnsServer = getDnsServerIp();
			LOGGER.info(dnsServer);
			Q_COUNT[] records = getRecordTypes();
			TRANSPORT_PROTOCOL transport = getTransportProtocol();
			String domain = getDomain();
			boolean recursive = isRecursiveSet();
			boolean dnssec = isDNSSECSet();
			boolean dnssecRRSig = dnssecRecordsRequestCheckBox.isSelected();
			boolean holdConnection = holdConectionCheckbox.isSelected();
			logMessage(dnsServer, domain, records, recursive, dnssec, transport, dnssecRRSig, holdConnection);
			sender = new MessageSender(recursive, dnssec, dnssecRRSig, domain, records, transport,
					APPLICATION_PROTOCOL.DNS, dnsServer);
			if(transport == TRANSPORT_PROTOCOL.TCP) {
				sender.setTcp(tcpConnection);
				sender.setCloseConnection(!holdConnection);
			}
			else {
				closeHoldedConnection();
				}
			sender.send();
			parser = new MessageParser(sender.getRecieveReply(), sender.getHeader(), transport);
			parser.parse();
			tcpConnection = sender.getTcp();
			setControls();
		} catch (NotValidDomainNameException | NotValidIPException | DnsServerIpIsNotValidException
				| MoreRecordsTypesWithPTRException | NonRecordSelectedException | TimeoutException | IOException
				| QueryIdNotMatchException | MessageTooBigForUDPException | CouldNotUseHoldConnectionException e) {
			String fullClassName = e.getClass().getSimpleName();
			LOGGER.info(fullClassName);
			numberOfMessagesValueLabel.setText("" + sender.getMessageSent());
			showAller(fullClassName);
		} catch (Exception e) {
			LOGGER.warning(e.toString());
			showAller("Exception");
		}

	}

	@FXML
	void onSavedDNSChoiseBoxFired(MouseEvent e) {
		customDNSRadioButton.setSelected(true);
		savedDNSChoiceBox.getItems().removeAll(savedDNSChoiceBox.getItems());
		savedDNSChoiceBox.getItems().addAll(settings.getDnsServers());
	}

	@FXML
	public void onDomainNameAction(ActionEvent e) {
		sendButtonFired(e);
	}

	@FXML
	public void clicked(MouseEvent event) {

		if (event.getClickCount() == 2) {
			@SuppressWarnings("unchecked")
			TreeView<String> v = (TreeView<String>) event.getSource();
			String value = v.getSelectionModel().getSelectedItem().getValue();
			String[] array = value.toString().split(": ");
			if (array.length != 1) {
				copyDataToClipBoard(array[1]);
			}
		}
	}

	@FXML
	public void onDomainNameChoiseBoxAction(ActionEvent event) {
		try {
			if (!savedDomainNamesChoiseBox.getValue().equals(null)
					&& !savedDomainNamesChoiseBox.getValue().equals("")) {
				domainNameTextField.setText(savedDomainNamesChoiseBox.getValue());
			}
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		}
	}

	@FXML
	public void onDomainNameChoiseBoxFired() {
		savedDomainNamesChoiseBox.getItems().removeAll(savedDomainNamesChoiseBox.getItems());
		savedDomainNamesChoiseBox.getItems().addAll(settings.getDomainNamesDNS());
	}

	@FXML
	public void onDnsServerNameChoiseBoxAction(ActionEvent event) {
		try {
			if (!savedDNSChoiceBox.getValue().equals(null) && !savedDNSChoiceBox.getValue().equals("")) {
				dnsServerTextField.setText(savedDNSChoiceBox.getValue());
				copyDataToClipBoard(dnsServerTextField.getText());
			}
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		}
	}

	@FXML
	public void domainNameKeyPressed(KeyEvent event) {
		autobinging(domainNameTextField.getText(), settings.getDomainNamesDNS(), savedDomainNamesChoiseBox);
	}

	@FXML
	public void dnsServerKeyPressed(KeyEvent event) {
		customDNSRadioButton.setSelected(true);
		if (Ip.isIpValid(dnsServerTextField.getText())) {
			copyDataToClipBoard(dnsServerTextField.getText());
		}
		autobinging(dnsServerTextField.getText(), settings.getDnsServers(), savedDNSChoiceBox);
	}

	@FXML
	public void deleteDomainNameHistoryFired(Event event) {
		settings.eraseDomainNames();
		savedDomainNamesChoiseBox.getItems().removeAll(savedDomainNamesChoiseBox.getItems());
}

	@FXML
	public void deleteDNSServerHistoryFired(Event event) {
		settings.eraseDNSServers();
		savedDNSChoiceBox.getItems().removeAll(savedDNSChoiceBox.getItems());
	}

	@FXML 
	public void transportProtocolAction(ActionEvent event) {
		if (tcpRadioButton.isSelected()) {
			holdConectionCheckbox.setDisable(false);
		}
		else {
			holdConectionCheckbox.setDisable(true);
			holdConectionCheckbox.setSelected(false);
			closeHoldedConnection();
		}
	}
	@FXML
	public void holdConnectionAction(ActionEvent event) {
		if(!holdConectionCheckbox.isSelected()) {
			closeHoldedConnection();
		}
	}
}
