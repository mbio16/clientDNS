package ui;

import java.util.logging.Logger;

import enums.APPLICATION_PROTOCOL;
import enums.Q_COUNT;
import enums.TRANSPORT_PROTOCOL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import models.DomainConvert;
import models.Ip;
import models.Language;
import models.MessageParser;
import models.MessageSender;

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
	private RadioButton otherDNSServerRadioButton;
	@FXML
	private RadioButton systemDNSRadioButton;
	@FXML
	private RadioButton savedDNSRadioButton;

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

	public DNSController() {
		super();
		LOGGER = Logger.getLogger(DNSController.class.getName());
	}

	public void initialize() {
		ipToggleGroup = new ToggleGroup();
		ipv4RadioButton.setToggleGroup(ipToggleGroup);
		ipv6RadioButton.setToggleGroup(ipToggleGroup);

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
		otherDNSServerRadioButton.setToggleGroup(dnsserverToggleGroup);
		savedDNSRadioButton.setToggleGroup(dnsserverToggleGroup);
		systemDNSRadioButton.setToggleGroup(dnsserverToggleGroup);

		domainNameToggleGroup = new ToggleGroup();
		domainNameChoiseBoxRadioButton.setToggleGroup(domainNameToggleGroup);
		domainNameTextFieldRadioButton.setToggleGroup(domainNameToggleGroup);

	}

	public void setLabels() {
		// define group to iterate over it
		TitledPane titlePaneArray[] = new TitledPane[] { domainNameTitledPane, ipTitledPane, transportTitledPane,
				dnssecTitledPane, recordTypeTitledPane, dnsServerTitledPane, iterativeTitledPane, responseTitledPane,
				queryTitledPane };

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

		// set system dns
		systemDNSRadioButton.setText(Ip.getPrimaryDNSIp());

		// setUserData
		String ip = Ip.getPrimaryDNSIp();
		systemDNSRadioButton.setText(ip);
		systemDNSRadioButton.setUserData(ip);
		cloudflareIpv4RadioButton.setUserData("1.1.1.1");
		googleIpv4RadioButton.setUserData("8.8.8.8");
		cznicIpv4RadioButton.setUserData("193.14.47.1");
		cloudflareIpv6RadioButton.setUserData("2606:4700:4700::1111");
		googleIpv6RadioButton.setUserData("2001:4860:4860::8888");
		cznicIpv6RadioButton.setUserData("2001:148f:ffff::1");

		// permform radio buttons actions
		onRadioButtonChange(null);
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
			newStage.show();
			Stage mainStage = (Stage) sendButton.getScene().getWindow();
			mainStage.close();
			controller.setLabels();
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("windowError"));
			alert.showAndWait();
		}
	}

	@FXML
	public void onRadioButtonChange(ActionEvent event) {
		otherDNSServerCheck();
		savedDNSServerCheck();
		domainNameRadioButtonChanged(event);
	}

	private void otherDNSServerCheck() {

		if (otherDNSServerRadioButton.isSelected()) {
			LOGGER.info("Other DNS server is enabled");
			dnsServerTextField.setDisable(false);
		} else {
			LOGGER.info("Other DNS server is not enabled");
			dnsServerTextField.setDisable(true);
		}

	}

	private void savedDNSServerCheck() {
		if (savedDNSRadioButton.isSelected()) {
			LOGGER.info("Saved DNS server is enabled");
			savedDNSChoiceBox.setDisable(false);
		} else {
			LOGGER.info("Saved DNS server is not enabled");
			savedDNSChoiceBox.setDisable(true);
		}
	}

	private String getDnsServerIp() {
		if (otherDNSServerRadioButton.isSelected() && Ip.isIpValid(dnsServerTextField.getText())) {
			settings.addDNSServer(dnsServerTextField.getText());
			return dnsServerTextField.getText();
		} else {
			if (savedDNSRadioButton.isSelected() && savedDNSChoiceBox.getValue() != null) {
				return savedDNSChoiceBox.getValue();
			} else {
				return (String) dnsserverToggleGroup.getSelectedToggle().getUserData().toString();
			}
		}
	}

	private String getDomain() {
		if (domainNameTextFieldRadioButton.isSelected()) {
			if (DomainConvert.isValidDomainName(domainNameTextField.getText())) {
				settings.addDNSDomain(domainNameTextField.getText());
				return domainNameTextField.getText();
			}
		}
		return savedDomainNamesChoiseBox.getValue();
	}

	@FXML
	public void sendButtonFired(ActionEvent event) {
		String domain = getDomain();
		String dnsServer = getDnsServerIp();
		LOGGER.info("Dns server: " + dnsServer);
		LOGGER.info("Domain to resolve: " + domain);
		Q_COUNT types[] = { Q_COUNT.A };
		try {
			MessageSender sender = new MessageSender(true, true, domain, types, TRANSPORT_PROTOCOL.UDP,
					APPLICATION_PROTOCOL.DNS, dnsServer);
			sender.send();
			MessageParser parser = new MessageParser(sender.getRecieveReply(), sender.getHeader());
			parser.parse();
			System.out.println(parser.toString());
			responseTimeValueLabel.setText("" + sender.getTimeElapsed());
			numberOfMessagesValueLabel.setText("" + sender.getMessagesSent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * if(domainNameTextFieldRadioButton.isSelected()) {
		 * settings.addDNSDomain(domainNameTextField.getText()); }
		 */

	}

}
