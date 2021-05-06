package ui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;
import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import enums.WIRESHARK_FILTER;
import exceptions.CouldNotUseHoldConnectionException;
import exceptions.DnsServerIpIsNotValidException;
import exceptions.InterfaceDoesNotHaveIPAddressException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.DomainConvert;
import models.Ip;
import models.MessageParser;
import models.MessageSender;
import models.TCPConnection;

public class DNSController extends MDNSController {

	public static final String FXML_FILE_NAME = "/fxml/DNS.fxml";

	// text fields
	@FXML
	private TextField dnsServerTextField;

	@FXML
	protected Label wiresharkLabel;

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

	@FXML
	protected RadioButton dnssecYesRadioButton;
	@FXML
	protected RadioButton dnssecNoRadioButton;
	// menu items
	@FXML
	protected MenuItem deleteDomainNameHistory;
	@FXML
	private MenuItem deleteDNSServersHistory;

	// checkboxes
	@FXML
	protected CheckBox soaCheckBox;
	@FXML
	protected CheckBox dnskeyCheckBox;
	@FXML
	protected CheckBox dsCheckBox;
	@FXML
	protected CheckBox caaCheckBox;
	@FXML
	protected CheckBox cnameCheckBox;
	@FXML
	protected CheckBox nsCheckBox;
	@FXML
	protected CheckBox mxCheckBox;
	@FXML
	protected CheckBox rrsigCheckBox;
	@FXML
	protected CheckBox holdConectionCheckbox;
	@FXML
	protected CheckBox nsec3paramCheckBox;
	@FXML
	protected RadioMenuItem justIp;
	@FXML
	protected RadioMenuItem ipAsFilter;
	@FXML
	private RadioMenuItem ipWithUDPAsFilter;
	@FXML
	protected RadioMenuItem ipwithTCPAsFilter;
	@FXML
	private RadioMenuItem ipWithUDPandTcpAsFilter;
	@FXML
	protected CheckBox nsec3CheckBox;

	// titledpane
	@FXML
	private TitledPane transportTitledPane;
	@FXML
	protected TitledPane dnsServerTitledPane;
	@FXML
	protected TitledPane iterativeTitledPane;

	// toogleGroup
	private ToggleGroup transportToggleGroup;
	private ToggleGroup iterativeToggleGroup;
	protected ToggleGroup dnsserverToggleGroup;
	protected ToggleGroup wiresharkFilterToogleGroup;
	protected ToggleGroup dnssecToggleGroup;
	// choice box
	@FXML
	private ComboBox<String> savedDNSChoiceBox;
	@FXML
	protected ImageView cloudflareIpv4ImageView;
	@FXML
	protected ImageView googleIpv4IamgeView;
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
		PROTOCOL = "DNS";

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

		wiresharkFilterToogleGroup = new ToggleGroup();
		justIp.setToggleGroup(wiresharkFilterToogleGroup);
		ipAsFilter.setToggleGroup(wiresharkFilterToogleGroup);
		ipWithUDPAsFilter.setToggleGroup(wiresharkFilterToogleGroup);
		ipwithTCPAsFilter.setToggleGroup(wiresharkFilterToogleGroup);
		ipWithUDPandTcpAsFilter.setToggleGroup(wiresharkFilterToogleGroup);
	}

	@FXML
	private void copyImageViewFired(MouseEvent event) {
		String ip = "";
		String response = "";
		String prefix = "ip";
		if (event.getSource() == custumImageView) {
			if (Ip.isIpValid(dnsServerTextField.getText()))
				ip = dnsServerTextField.getText();
		} else {

			ImageView v = (ImageView) event.getSource();
			ip = v.getUserData().toString();

		}
		if (Ip.isIpv6Address(ip))
			prefix = "ipv6";
		switch ((WIRESHARK_FILTER) wiresharkFilterToogleGroup.getSelectedToggle().getUserData()) {
		case JUST_IP:
			response = ip;
			break;
		case IP_FILTER:
			response = prefix + ".addr == " + ip;
			break;
		case IP_WITH_UDP:
			response = prefix + ".addr == " + ip + " && udp.port == 53";
			break;
		case IP_WITH_TCP:
			response = prefix + ".addr == " + ip + " && tcp.port == 53";
			break;
		case IP_WITH_UDP_AND_TCP:
			response = prefix + ".addr == " + ip + " && (udp.port == 53 || tcp.port == 53)";
		default:
			break;
		}
		LOGGER.info("Copy to clipboard: " + response);
		copyDataToClipBoard(response);
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

		} else {
			systemIpv6DNSRadioButton.setText(ipDns.getIpv6DnsServer());
			systemIpv6DNSRadioButton.setUserData(ipDns.getIpv6DnsServer());
			systemIpv6DNSIamgeView.setDisable(false);
			systemIpv6DNSIamgeView.setUserData(ipDns.getIpv6DnsServer());
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

		RadioMenuItem[] radioMenuItemsArray = new RadioMenuItem[] { justIp, ipAsFilter, ipWithUDPAsFilter,
				ipwithTCPAsFilter, ipWithUDPandTcpAsFilter };
		// set labels to current language in menu
		backMenuItem.setText(language.getLanguageBundle().getString(backMenuItem.getId()));
		actionMenu.setText(language.getLanguageBundle().getString(actionMenu.getId()));
		languageMenu.setText(language.getLanguageBundle().getString(languageMenu.getId()));
		historyMenu.setText(language.getLanguageBundle().getString(historyMenu.getId()));
		for (TitledPane titledPane : titlePaneArray) {
			titledPane.setText(language.getLanguageBundle().getString(titledPane.getId()));
		}

		for (RadioButton radioButton : radioButtonArray) {
			radioButton.setText(language.getLanguageBundle().getString(radioButton.getId()));
		}

		for (Label label : labelsArray) {
			label.setText(language.getLanguageBundle().getString(label.getId()));
		}

		for (RadioMenuItem item : radioMenuItemsArray) {
			item.setText(language.getLanguageBundle().getString(item.getId()));
		}
		// set sendButton
		sendButton.setText(language.getLanguageBundle().getString(sendButton.getId()));

		holdConectionCheckbox.setText(language.getLanguageBundle().getString(holdConectionCheckbox.getId()));
		dnssecRecordsRequestCheckBox
				.setText(language.getLanguageBundle().getString(dnssecRecordsRequestCheckBox.getId()));

		setLanguageRadioButton();
		// set system dns
		setSystemDNS();
		// setUserData

		setUserDataDnsServers();
		setUserDataRecords();
		setUserDataTransportProtocol();
		setUserDataWiresharkRadioMenuItem();
		// permform radio buttons actions
		onRadioButtonChange(null);

		responseTreeView.setStyle("-fx-font-size: 12");
		requestTreeView.setStyle("-fx-font-size: 12");

		copyRequestJsonButton.setText(language.getLanguageBundle().getString(copyRequestJsonButton.getId()));
		copyResponseJsonButton.setText(language.getLanguageBundle().getString(copyResponseJsonButton.getId()));
		deleteDomainNameHistory.setText(language.getLanguageBundle().getString(deleteDomainNameHistory.getId()));
		deleteDNSServersHistory.setText(language.getLanguageBundle().getString(deleteDNSServersHistory.getId()));

		wiresharkLabel.setText(language.getLanguageBundle().getString(wiresharkLabel.getId()));
		setTitle();
		interfaceMenu.setText(language.getLanguageBundle().getString(interfaceMenu.getId()));
	}

	private void setUserDataWiresharkRadioMenuItem() {
		justIp.setUserData(WIRESHARK_FILTER.JUST_IP);
		ipAsFilter.setUserData(WIRESHARK_FILTER.IP_FILTER);
		ipWithUDPAsFilter.setUserData(WIRESHARK_FILTER.IP_WITH_UDP);
		ipwithTCPAsFilter.setUserData(WIRESHARK_FILTER.IP_WITH_TCP);
		ipWithUDPandTcpAsFilter.setUserData(WIRESHARK_FILTER.IP_WITH_UDP_AND_TCP);
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
		cznicIpv4RadioIamgeView.setUserData("193.17.47.1");
		cloudflareIpv6ImageView.setUserData("2606:4700:4700::1111");
		googleIpv6ImagaView.setUserData("2001:4860:4860::8888");
		cznicIpv6ImageView.setUserData("2001:148f:ffff::1");

	}
	
	protected void setUserDataRecords() {
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
		nsec3paramCheckBox.setUserData(Q_COUNT.NSEC3PARAM);
		anyCheckBox.setUserData(Q_COUNT.ANY);
	}

	public void loadDataFromSettings() {
		savedDomainNamesChoiseBox.getItems().addAll(settings.getDomainNamesDNS());
		savedDNSChoiceBox.getItems().addAll(settings.getDnsServers());
	}

	@FXML
	private void onRadioButtonChange(ActionEvent event) {
		if (dnsserverToggleGroup.getSelectedToggle().getUserData() != null) {
			dnsServerTextField.setText("");
		}

	}

	private String getDnsServerIp() throws DnsServerIpIsNotValidException, UnknownHostException {
		if(dnsServerTextField.getText().equals("") &&  customDNSRadioButton.isSelected()) throw new DnsServerIpIsNotValidException();
		if (DomainConvert.isValidDomainName(dnsServerTextField.getText()) && customDNSRadioButton.isSelected()) {
			InetAddress ipaddress = InetAddress.getByName(dnsServerTextField.getText());
			System.out.println("IP address: " + ipaddress.getHostAddress());
			String ipAddr = ipaddress.getHostAddress().toString();
			Alert info = new Alert(Alert.AlertType.INFORMATION);
			info.setTitle(language.getLanguageBundle().getString("translated"));
			info.setContentText(dnsServerTextField.getText() + " "
					+ language.getLanguageBundle().getString("rootServerWasTranslated") + ipAddr);
			info.initModality(Modality.APPLICATION_MODAL);
			info.initOwner((Stage) sendButton.getScene().getWindow());
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

	protected String getDomain() throws NotValidDomainNameException {
		try {
			String domain = (domainNameTextField.getText());
			LOGGER.info("Domain name: " + domain);
			if(domain == "") {
				throw new NotValidDomainNameException();
			}

			if ((domain.contains(".arpa")) && ptrCheckBox.isSelected()) {
				return domain;
			}
			if ((Ip.isIPv4Address(domain) || Ip.isIpv6Address(domain)) && ptrCheckBox.isSelected()) {
				LOGGER.info("PTR record request");
				return Ip.getIpReversed(domain);
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

	protected Q_COUNT[] getRecordTypes() throws MoreRecordsTypesWithPTRException, NonRecordSelectedException {
		ArrayList<Q_COUNT> list = new ArrayList<Q_COUNT>();
		CheckBox[] checkBoxArray = { aCheckBox, aaaaCheckBox, nsCheckBox, mxCheckBox, soaCheckBox, cnameCheckBox,
				ptrCheckBox, dnskeyCheckBox, dsCheckBox, caaCheckBox, txtCheckBox, rrsigCheckBox, nsecCheckBox,
				nsec3CheckBox, nsec3paramCheckBox,anyCheckBox };
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

	private void closeHoldedConnection() {
		try {
			if (tcpConnection != null)
				tcpConnection.closeAll();
		} catch (Exception e) {
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
				+ dnssecRRsig + "\n" + "Transport protocol: " + transport + "\n" + "Hold connection: " + holdConnection
				+ "\n" + "Application protocol: " + APPLICATION_PROTOCOL.DNS);
	}



	@FXML
	protected void sendButtonFired(ActionEvent event) {
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
			if (transport == TRANSPORT_PROTOCOL.TCP) {
				sender.setTcp(tcpConnection);
				sender.setCloseConnection(!holdConnection);
			} else {
				closeHoldedConnection();
			}
			sender.setInterfaceToSend(getInterface());
			sender.send();
			parser = new MessageParser(sender.getRecieveReply(), sender.getHeader(), transport);
			parser.parse();
			tcpConnection = sender.getTcp();
			setControls();
		} catch (NotValidDomainNameException | NotValidIPException | DnsServerIpIsNotValidException
				| MoreRecordsTypesWithPTRException | NonRecordSelectedException | TimeoutException | IOException
				| QueryIdNotMatchException | MessageTooBigForUDPException | CouldNotUseHoldConnectionException | InterfaceDoesNotHaveIPAddressException e) {
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
	private void onSavedDNSChoiseBoxFired(MouseEvent e) {
		customDNSRadioButton.setSelected(true);
		savedDNSChoiceBox.getItems().removeAll(savedDNSChoiceBox.getItems());
		savedDNSChoiceBox.getItems().addAll(settings.getDnsServers());
	}
	@FXML
	protected void onDomainNameChoiseBoxAction(ActionEvent event) {
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
	protected void onDomainNameChoiseBoxFired() {
		savedDomainNamesChoiseBox.getItems().removeAll(savedDomainNamesChoiseBox.getItems());
		savedDomainNamesChoiseBox.getItems().addAll(settings.getDomainNamesDNS());
	}

	@FXML
	private void onDnsServerNameChoiseBoxAction(ActionEvent event) {
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
	protected void domainNameKeyPressed(KeyEvent event) {		controlKeys(event, domainNameTextField);
		autobinging(domainNameTextField.getText(), settings.getDomainNamesDNS(), savedDomainNamesChoiseBox);
	}

	@FXML
	private void dnsServerKeyPressed(KeyEvent event) {
		customDNSRadioButton.setSelected(true);
		controlKeys(event, dnsServerTextField);
		autobinging(dnsServerTextField.getText(), settings.getDnsServers(), savedDNSChoiceBox);
	}




	@FXML
	private void deleteDomainNameHistoryFired(Event event) {
		settings.eraseDomainNames();
		savedDomainNamesChoiseBox.getItems().removeAll(savedDomainNamesChoiseBox.getItems());
	}

	@FXML
	private void deleteDNSServerHistoryFired(Event event) {
		settings.eraseDNSServers();
		savedDNSChoiceBox.getItems().removeAll(savedDNSChoiceBox.getItems());
	}

	@FXML
	private void transportProtocolAction(ActionEvent event) {
		if (tcpRadioButton.isSelected()) {
			holdConectionCheckbox.setDisable(false);
		} else {
			holdConectionCheckbox.setDisable(true);
			holdConectionCheckbox.setSelected(false);
			closeHoldedConnection();
		}
	}

	@FXML
	private void holdConnectionAction(ActionEvent event) {
		if (!holdConectionCheckbox.isSelected()) {
			closeHoldedConnection();
		}
	}
}
