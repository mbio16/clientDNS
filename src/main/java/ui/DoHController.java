package ui;

import java.io.IOException;
import java.net.UnknownHostException;

import org.json.simple.parser.ParseException;
import enums.APPLICATION_PROTOCOL;
import enums.DOH_FORMAT;
import enums.Q_COUNT;
import enums.WIRESHARK_FILTER;
import exceptions.CouldNotUseHoldConnectionException;
import exceptions.CustomEndPointException;
import exceptions.HttpCodeException;
import exceptions.MessageTooBigForUDPException;
import exceptions.MoreRecordsTypesWithPTRException;
import exceptions.NonRecordSelectedException;
import exceptions.NotValidDomainNameException;
import exceptions.NotValidIPException;
import exceptions.OtherHttpException;
import exceptions.TimeoutException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Ip;
import models.MessageParser;
import models.MessageSender;

public class DoHController extends DNSController {
	
	public static final String FXML_FILE_NAME = "/fxml/DoH.fxml";
	
	@FXML
	private TextArea requestTextArea;
	@FXML
	private RadioButton jsonApiRadioButton;
	@FXML
	private RadioButton wireRadioButton;
	@FXML
	private RadioButton cloudflareRadionButton;
	@FXML
	private RadioButton googleRadioButton;
	@FXML
	private ImageView cloudflareImageView;
	@FXML
	private ImageView googleImageView;
	@FXML 
	private ImageView customDnsImageView;
	@FXML
	private TextArea responseTextArea;
	@FXML
	private RadioButton customEndPointRadioButton;
	@FXML
	private TextField customEndPointTextField;
	
	private ToggleGroup formatDoHToggleGroup;
	
	
	public DoHController() {
	super();
	PROTOCOL = "DNS over Https";
	}
	
	public void initialize() {
		formatDoHToggleGroup = new ToggleGroup();
		dnssecToggleGroup = new ToggleGroup();
		dnsserverToggleGroup = new ToggleGroup();
		wiresharkFilterToogleGroup = new ToggleGroup();
		
		jsonApiRadioButton.setToggleGroup(formatDoHToggleGroup);
		wireRadioButton.setToggleGroup(formatDoHToggleGroup);
		
		dnssecYesRadioButton.setToggleGroup(dnssecToggleGroup);
		dnssecNoRadioButton.setToggleGroup(dnssecToggleGroup);
		
		cloudflareRadionButton.setToggleGroup(dnsserverToggleGroup);
		googleRadioButton.setToggleGroup(dnsserverToggleGroup);
		customEndPointRadioButton.setToggleGroup(dnsserverToggleGroup);
		
		justIp.setToggleGroup(wiresharkFilterToogleGroup);
		ipAsFilter.setToggleGroup(wiresharkFilterToogleGroup);
		ipwithTCPAsFilter.setToggleGroup(wiresharkFilterToogleGroup);
	}
	
	public void setLabels() {
			TitledPane [] titlePanes = new TitledPane [] {
					domainNameTitledPane,
					queryTitledPane,
					recordTypeTitledPane,
					iterativeTitledPane,
					responseTitledPane
			};
			Menu [] menuItems = new Menu [] {
					actionMenu,
					languageMenu,
					historyMenu,
			};
			for (TitledPane titledPane : titlePanes) {
				titledPane.setText(language.getLanguageBundle().getString(titledPane.getId()));
			}
			for (Menu menu : menuItems) {
				menu.setText(language.getLanguageBundle().getString(menu.getId()));
			}
		MenuItem [] menuItems1 = new MenuItem [] {
			deleteDomainNameHistory,
			justIp,
			ipAsFilter,
			ipwithTCPAsFilter
		};
		for (MenuItem menuItem : menuItems1) {
			menuItem.setText(language.getLanguageBundle().getString(menuItem.getId()));
		}
		wiresharkLabel.setText(language.getLanguageBundle().getString(wiresharkLabel.getId()));
			setUserDataRecords();
			setFormatUserData();
			setDNSServerUserData();
			setTitle();
			setLanguageRadioButton();
			setWiresharkUserData();
			setImageViewUserData();
		customEndPointTextField.setPromptText(language.getLanguageBundle().getString(customEndPointTextField.getId()));
	}
	
	private void setImageViewUserData() {
		cloudflareImageView.setUserData(ipDns.getClouflareIp());
		googleImageView.setUserData(ipDns.getGoogleIp());
		}

	private void setWiresharkUserData() {
		justIp.setUserData(WIRESHARK_FILTER.JUST_IP);
		ipAsFilter.setUserData(WIRESHARK_FILTER.IP_FILTER);
		ipwithTCPAsFilter.setUserData(WIRESHARK_FILTER.IP_WITH_TCP);
	}
	private void setFormatUserData() {
		jsonApiRadioButton.setUserData(DOH_FORMAT.JSON_API);
		wireRadioButton.setUserData(DOH_FORMAT.WIRE);
	}
	
	private void setDNSServerUserData() {
		cloudflareRadionButton.setUserData("cloudflare-dns.com/dns-query");
		googleRadioButton.setUserData("dns.google/resolve");
		customDnsImageView.setUserData("custom");
	}
	@FXML
	private void customDNSAction() {
			customEndPointRadioButton.setSelected(true);
	}
	@FXML
	private void predefineDNSAction(ActionEvent event) {
		if(cloudflareRadionButton.isSelected() || googleRadioButton.isSelected()){
			customEndPointTextField.setText("");	
		}
	}
	
	@FXML
	private void copyImageViewFired(MouseEvent event) {
		ImageView image = (ImageView) event.getSource();
		String ip;
		if(image.getUserData().equals("custom")) {
			try {
				ip = getCustomIp();
			}
			catch (CustomEndPointException e) {
				showAller("CustomEndPointException");
				return;
			}
		}else {
			ip = (String) image.getUserData();
		}
		String result = "";
		System.out.println(ip);
		switch ((WIRESHARK_FILTER) wiresharkFilterToogleGroup.getSelectedToggle().getUserData()) {
		case JUST_IP:
			result = ip;
			break;
		case IP_FILTER:
			result = "ip.addr == " + ip;
			break;
		case IP_WITH_TCP:
			result = "ip.addr == " + ip + " && tcp.port == 443";
			break;
		default:
			break;
		}
		copyDataToClipBoard(result);
	}
	
	private String getCustomIp() throws CustomEndPointException{
		String ip = "";
		String domain = customEndPointTextField.getText();
			if(Ip.isIpValid(domain)) 
				ip = domain;
			else {
				String splited [] = domain.split("/");
				ipDns.getUserDoHurlIP(splited[0]);
				ip = ipDns.getUserInputIp();
			}
			return ip;
	}

	private String getResolverAndupdateItIp() throws UnknownHostException, CustomEndPointException {
		String fullName;
		String justDomain;
		if(customEndPointRadioButton.isSelected()) {
			fullName = customEndPointTextField.getText();
		}
		else {
			fullName = (String) dnsserverToggleGroup.getSelectedToggle().getUserData();
		}
		
		justDomain = fullName.split("/")[0];
		switch (justDomain) {
		case "dns.google":
			ipDns.updateGoogleIp();
			//System.out.println("updated google");
			break;
		case "cloudflare-dns.com":
			ipDns.updateCloudflareIp();
			//System.out.println("updated cloudflare");
		default:			
				getCustomIp();
			break;
		}
		return fullName;
	}
	
	@FXML
	protected void sendButtonFired(ActionEvent event) {
		try {
		String domain = getDomain();
		boolean dnssec = dnssecYesRadioButton.isSelected();
		boolean signatures = dnssecRecordsRequestCheckBox.isSelected();
		Q_COUNT [] qcount = getRecordTypes();
		String resolverURL = getResolverAndupdateItIp();
		logRequest(dnssec, signatures, domain, qcount, resolverURL);
		sender =  new MessageSender(
				false, //recursion
				dnssec, //dnssec
				signatures, //rrRecords
				domain, //domain as string
				qcount, //records
				null, //
				APPLICATION_PROTOCOL.DOH, // application protocol
				resolverURL);
		sender.send();
		parser = new MessageParser(sender.getHttpResponse());
		setControls();
		}
		catch(HttpCodeException e) {
			Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("HttpCodeException") + e.getCode());
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner((Stage) sendButton.getScene().getWindow());
			alert.show();
		}
		catch (
				NotValidDomainNameException |
				NotValidIPException |
				MoreRecordsTypesWithPTRException | 
				NonRecordSelectedException | 
				TimeoutException | 
				IOException|
				MessageTooBigForUDPException |
				CouldNotUseHoldConnectionException |
				OtherHttpException |
				ParseException |
				CustomEndPointException
				 e) {
			e.printStackTrace();
			String fullClassName = e.getClass().getSimpleName();
			LOGGER.info(fullClassName);
			showAller(fullClassName);
			} 
		}

	private void logRequest(boolean dnssec, boolean signatures, String domain, Q_COUNT [] qcount, String resolverURL) {
		String records = "";
		for (Q_COUNT q_COUNT : qcount) {
			records += q_COUNT + ",";
		}
		LOGGER.info("DoH:\n " +
		"dnssec: " + dnssec + "\n" +
		"signatures: " + signatures + "\n" +
		"domain: " + domain + "\n" +
		"records: " + records  + "\n" + 
		"resovlerURL: " + resolverURL);
		
	}
	@Override
	protected void setControls() {
		requestTextArea.setText(sender.getDoHRequest());
		responseTimeValueLabel.setText(sender.getTimeElapsed()+"");
		numberOfMessagesValueLabel.setText(sender.getMessageSent()+"");
		responseTextArea.setText(parser.getAsJsonString());
		queryTitledPane.setText(language.getLanguageBundle().getString(queryTitledPane.getId().toString()) + " ("
				+ sender.getByteSizeQuery() + " B)");
		responseTitledPane.setText(language.getLanguageBundle().getString(queryTitledPane.getId().toString()) + " ("
				+ sender.getByteSizeResponseDoH() + " B)");
	}
	public void loadDataFromSettings() {
		this.savedDomainNamesChoiseBox.getItems().setAll(settings.getDomainNamesDNS());
	}

}
