package com.mp3player.fx.app;

import java.io.IOException;

import com.mp3player.fx.icons.FXIcons;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SettingsWindow {
	private Stage stage;

	@FXML private TabPane tabPane;
	@FXML private Tab generalTab, audioTab, libraryTab, networkTab;

	public SettingsWindow(Window owner) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
		loader.setController(this);
		BorderPane root = new BorderPane();
		try {
			root.setCenter(loader.load());
		} catch (IOException e) {
			root.setCenter(new Label(e.getMessage()));
		}
		stage = new Stage();
		stage.initOwner(owner);
		stage.initModality(Modality.NONE);
		stage.setTitle("Settings - MX Player");
		stage.getIcons().add(FXIcons.get("Settings.png", 32).getImage());
		stage.setScene(new Scene(root));
	}

	public void show() {
		stage.show();
	}

	public void showGeneralTab() {
		Platform.runLater(() -> tabPane.getSelectionModel().select(generalTab));
	}

	public void showLibraryTab() {
		Platform.runLater(() -> tabPane.getSelectionModel().select(libraryTab));
	}
}
