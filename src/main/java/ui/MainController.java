package ui;

import java.awt.Desktop;
import java.net.URI;
import java.util.logging.Logger;
import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Language;
import models.Settings;

public class MainController extends GeneralController {
	// FXML Components
	// language radio buttons
	@FXML
	private RadioButton englishLangRadioButton;
	@FXML
	private RadioButton czechLangRadioButton;
	// button to chose protocol
	@FXML
	private Button dnsButton;
	@FXML
	private Button llmrButton;
	@FXML
	private Button mdnsButton;
	@FXML
	private Button dohButton;
	@FXML
	private Button dotButton;
	@FXML
	private Button reportBugButton;
	// labels for protocol group
	@FXML
	private Label basicDNSLabel;
	@FXML
	private Label multicastDNSLabel;
	@FXML
	private Label encryptedDNSLabel;
	// help image
	@FXML
	private Label dnsButtonHelp;
	@FXML
	private ImageView llmrButtonHelp;
	@FXML
	private ImageView mdnsButtonHelp;
	@FXML
	private ImageView dohButtonHelp;
	@FXML
	private ImageView dotButtonHelp;

	private static final String BUG_URL = "https://github.com/mbio16/clientDNS/issues";
	private ToggleGroup languagegroup;

	public static final String FXML_FILE_NAME = "/fxml/Main.fxml";

	public void initialize() {

		//
		LOGGER = Logger.getLogger(DNSController.class.getName());

		// setup toogle group
		languagegroup = new ToggleGroup();
		czechLangRadioButton.setToggleGroup(languagegroup);
		englishLangRadioButton.setToggleGroup(languagegroup);
		PROTOCOL = "DNS";
	}

	@FXML
	private void languageChanged(ActionEvent event) {
		language.changeLanguageBundle(czechLangRadioButton.isSelected());
		setLabels();
	}

	@FXML
	private void definedButtonFired(ActionEvent event) {

		Button button = (Button) event.getSource();
		String fxml_file = (String) button.getUserData();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_file));
			Stage newStage = new Stage();

			newStage.setScene(new Scene((Parent) loader.load()));
			GeneralController controller = (GeneralController) loader.getController();

			Stage oldStage = (Stage) dnsButton.getScene().getWindow();
			newStage.setX(oldStage.getX());
			newStage.setY(oldStage.getY());
			newStage.getIcons().add(new Image(Main.ICON_URI));
			controller.setLanguage(language);
			controller.setSettings(settings);
			controller.setIpDns(ipDns);
			controller.setLabels();
			controller.loadDataFromSettings();
			controller.networkInterfaces();
			newStage.show();
			oldStage.close();

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe("Could not open new window:" + e.toString());
			Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("windowError"));
			alert.showAndWait();
		}
	}

	@FXML
	private void buttonFired(ActionEvent event) {
		LOGGER.warning("Calling a module which is not implemented");
		Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("notImplemented"));
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner((Stage) dnsButton.getScene().getWindow());
		alert.showAndWait();

	}

	@FXML
	private void reportBugButtonFired(ActionEvent event) {
		final Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(new URI(BUG_URL));
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, language.getLanguageBundle().getString("bugButtonError"));
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner((Stage) dnsButton.getScene().getWindow());
			alert.showAndWait();
		}
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public void setLabels() {
		basicDNSLabel.setText(language.getLanguageBundle().getString(basicDNSLabel.getId()));
		dnsButton.setUserData(DNSController.FXML_FILE_NAME);
		multicastDNSLabel.setText(language.getLanguageBundle().getString(multicastDNSLabel.getId()));
		mdnsButton.setUserData(MDNSController.FXML_FILE_NAME);
		encryptedDNSLabel.setText(language.getLanguageBundle().getString(encryptedDNSLabel.getId()));
		dohButton.setUserData(DoHController.FXML_FILE_NAME);
		reportBugButton.setText(language.getLanguageBundle().getString(reportBugButton.getId()));
		Stage stage = (Stage) basicDNSLabel.getScene().getWindow();
		stage.setTitle(language.getLanguageBundle().getString(APP_TITTLE) + " " + PROTOCOL);

		switch (language.getCurrentLanguage()) {
		case Language.CZECH:
			czechLangRadioButton.setSelected(true);
			englishLangRadioButton.setSelected(false);
			break;
		case Language.ENGLISH:
			czechLangRadioButton.setSelected(false);
			englishLangRadioButton.setSelected(true);
			break;
		default:
			break;
		}
	}
}
