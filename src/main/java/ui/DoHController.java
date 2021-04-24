package ui;

import com.sun.jdi.event.Event;

import enums.DOH_FORMAT;
import enums.WIRESHARK_FILTER;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

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
	
	private ToggleGroup formatDoHToggleGroup;
	
	
	public DoHController() {
	super();
	
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
	}
	@FXML
	private void copyImageViewFired(MouseEvent event) {
		ImageView image = (ImageView) event.getSource();
		String ip = (String) image.getUserData();
		String result = "";
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
	public void loadDataFromSettings() {
		this.savedDomainNamesChoiseBox.getItems().setAll(settings.getDomainNamesDNS());
	}

}
