package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.Ip;

public class InfoWindowController{

	@FXML private Label infoLabel;
	
	
	public void initialize() {
	//	infoLabel.setText("ahoj");
	}
	public void setText(String text) {
		//infoLabel.setText(text);
	}
 
	public Ip getIp() {
		return new Ip();
	} 
}
