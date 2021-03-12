package ui;

import java.util.ArrayList;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
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
	protected MenuItem backMenuItem;
	@FXML
	protected Menu languageMenu;
	@FXML
	protected RadioMenuItem czechRadioButton;
	@FXML
	protected RadioMenuItem englishRadioButton;
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
	protected RadioButton ipv4RadioButton;
	@FXML
	protected RadioButton ipv6RadioButton;
	@FXML
	protected RadioButton dnssecYesRadioButton;
	@FXML
	protected RadioButton dnssecNoRadioButton;

	// checkboxes
	@FXML
	protected CheckBox aCheckBox;
	@FXML
	protected CheckBox aaaaCheckBox;
	@FXML
	protected CheckBox nsCheckBox;
	@FXML
	protected CheckBox mxCheckBox;
	@FXML
	protected CheckBox cnameCheckBox;
	@FXML
	protected CheckBox ptrCheckBox;
	@FXML
	protected CheckBox dnssecRecordsRequestCheckBox;
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
	protected ToggleGroup ipToggleGroup;
	protected ToggleGroup dnssecToggleGroup;
	protected ToggleGroup domainNameToggleGroup;

	@FXML
	protected ChoiceBox<String> savedDomainNamesChoiseBox;

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

		dnssecToggleGroup = new ToggleGroup();
		dnssecYesRadioButton.setToggleGroup(dnssecToggleGroup);
		dnssecNoRadioButton.setToggleGroup(dnssecToggleGroup);

	}

	public void setLabels() {
		// define group to iterate over it
		TitledPane titlePaneArray[] = new TitledPane[] { domainNameTitledPane, ipTitledPane, dnssecTitledPane,
				recordTypeTitledPane, responseTitledPane, queryTitledPane };

		// same for radio buttons
		RadioButton[] radioButtonArray = new RadioButton[] { dnssecYesRadioButton, dnssecNoRadioButton, };

		Label[] labelsArray = new Label[] { responseTimeLabel, numberOfMessagesLabel };

		dnssecRecordsRequestCheckBox
				.setText(language.getLanguageBundle().getString(dnssecRecordsRequestCheckBox.getId()));
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
	private void backButtonFirred(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(MainController.FXML_FILE_NAME));
			Stage newStage = new Stage();
			newStage.setScene(new Scene((Parent) loader.load()));
			newStage.setTitle(APP_TITTLE);
			GeneralController controller = (GeneralController) loader.getController();
			controller.setLanguage(language);
			controller.setSettings(settings);
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
	protected void sendButtonFired(ActionEvent event) {

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

	protected void autobinging(String textFromField, ArrayList<String> fullArray, ChoiceBox<String> box) {
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
	protected void expandAllRequestOnClick(Event event) {
		expandAll(requestTreeView);
	}

	@FXML
	protected void expandAllResponseOnClick(Event event) {
		expandAll(responseTreeView);
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
