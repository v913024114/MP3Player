package com.mp3player.fx.app;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.aquafx_project.AquaFx;
import com.mp3player.fx.FileDropOverlay;
import com.mp3player.fx.PlayerControl;
import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mp3player.player.PlayerStatus;

public class PlayerWindow implements Initializable {
	private Scene scene;
	private Stage stage;

	@FXML private Menu currentSongMenu, settingsMenu;

	private PlayerControl control;

	private PlayerStatus status;


	public PlayerWindow(PlayerStatus status, Stage stage) throws IOException {
		this.stage = stage;
		this.status = status;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("mp3player.fxml"));
		loader.setController(this);
		BorderPane root = loader.load();

		control = new PlayerControl();
		control.setDuration(60*3.6);
		root.setCenter(control);

		FileDropOverlay overlay = new FileDropOverlay(root);
		overlay.setActionGenerator(files -> {
			ToggleButton button = new ToggleButton("Play");
			button.setOnAction(e -> {
				currentSongMenu.setText(files.get(0).getName());
			});
			return Arrays.asList(button);
		});

		stage.setScene(scene = new Scene(root));
		scene.getStylesheets().add(getClass().getResource("defaultstyle.css").toExternalForm());

		stage.setTitle("MX Player");
		stage.getIcons().add(new Image(getClass().getResource("window-icon.png").toExternalForm()));
		stage.show();
	}

	@FXML
	public void setStyle(ActionEvent e) {
		MenuItem item = (MenuItem) e.getTarget();
		String text = item.getText();
		if(text.equals("AquaFX")) {
			AquaFx.style();
		} else {
			Application.setUserAgentStylesheet(text.toUpperCase());
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		settingsMenu.setText(null);
		settingsMenu.setGraphic(loadIcon("settings.png", 20));
		currentSongMenu.setGraphic(loadIcon("file.png", 20));
	}


	private static ImageView loadIcon(String filename, double height) {
		ImageView settingsImage = new ImageView(new Image(PlayerWindow.class.getResource(filename).toExternalForm()));
		settingsImage.setFitHeight(height * dpiFactor());
		settingsImage.setPreserveRatio(true);
		return settingsImage;
	}
    private static double dpiFactor() {
	    return Font.getDefault().getSize() / 12.0;
	}
}
