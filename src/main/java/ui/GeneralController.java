package ui;

import java.util.List;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import models.Ip;
import models.Language;
import models.Settings;

public class GeneralController {

	protected Language language;
	public static final String APP_TITTLE = "DNS klient";
	protected Settings settings;
	protected Logger LOGGER;
	protected Ip ipDns;

	public Settings getSettings() {
		return settings;
	}

	public void setIpDns(Ip ip) {
		this.ipDns = ip;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
		// System.out.println("Language load to another window");
	}

	public void setLabels() {
		// To be overrited
	}

	public void loadDataFromSettings() {
		// to be overrited
	}
	protected static int getActiveStageLocationX(Scene scene){
	    List<Screen> interScreens = Screen.getScreensForRectangle(scene.getWindow().getX(),
	            scene.getWindow().getY(),
	            scene.getWindow().getWidth(),
	            scene.getWindow().getHeight());
	    Screen activeScreen = (Screen) interScreens.get(0);
	    Rectangle2D r = activeScreen.getBounds();
	    double position = r.getMinX();
	    return (int) position;
	}
	protected static int getActiveStageLocationY(Scene scene){
	    List<Screen> interScreens = Screen.getScreensForRectangle(scene.getWindow().getX(),
	            scene.getWindow().getY(),
	            scene.getWindow().getWidth(),
	            scene.getWindow().getHeight());
	    Screen activeScreen = (Screen) interScreens.get(0);
	    Rectangle2D r = activeScreen.getBounds();
	    double position = r.getMinY();
	    return (int) position;
	}
}
