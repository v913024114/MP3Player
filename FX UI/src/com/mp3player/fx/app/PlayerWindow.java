package com.mp3player.fx.app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.aquafx_project.AquaFx;
import com.mp3player.fx.FileDropOverlay;
import com.mp3player.fx.PlayerControl;
import com.mp3player.vdp.RemoteFile;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
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
	private PlayerStatusWrapper properties;


	public PlayerWindow(PlayerStatus status, Stage stage) throws IOException {
		this.stage = stage;
		this.status = status;
		properties = new PlayerStatusWrapper(status);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("mp3player.fxml"));
		loader.setController(this);
		BorderPane root = loader.load();

		control = new PlayerControl();
		control.durationProperty().bind(properties.durationProperty());
		control.positionProperty().bindBidirectional(properties.positionProperty());
		control.playingProperty().bindBidirectional(properties.playingProperty());
		control.mediaSelectedProperty().bind(properties.mediaSelectedProperty());
		control.playlistAvailableProperty().bind(properties.playlistAvailableProperty());
		root.setCenter(control);

		FileDropOverlay overlay = new FileDropOverlay(root);
		overlay.setActionGenerator(files -> generateDropButtons(files));

		stage.setScene(scene = new Scene(root));
		scene.getStylesheets().add(getClass().getResource("defaultstyle.css").toExternalForm());

		stage.setTitle("MX Player");
		stage.getIcons().add(new Image(getClass().getResource("window-icon.png").toExternalForm()));
	}

	private List<ToggleButton> generateDropButtons(List<File> files) {
		List<ToggleButton> result = new ArrayList<>(4);

		boolean cold = status.getPlaylist().isEmpty();

		if(cold && files.size() == 1) {
			ToggleButton playSingle = new ToggleButton("Play");
			playSingle.setOnAction(e -> {
				RemoteFile remoteFile = properties.getStatus().getVdp().mountFile(files.get(0));
				String mediaID = status.getPlaylist().add(remoteFile);
				status.getTarget().setTargetMedia(mediaID);
				status.getTarget().setTargetPlaying(true);
			});
			result.add(playSingle);

			List<File> allAudioFiles = AudioFiles.allAudioFilesIn(files.get(0).getParentFile());
			if(allAudioFiles.size() > 1) {
				ToggleButton playFolder = new ToggleButton("Play folder");
				playFolder.setOnAction(e -> {
					RemoteFile remoteFile = properties.getStatus().getVdp().mountFile(files.get(0));
					String mediaID = status.getPlaylist().add(remoteFile);
					status.getTarget().setTargetMedia(mediaID);
					status.getTarget().setTargetPlaying(true);
				});
				result.add(playFolder);

			}
		}

//		if(!cold)


		return result;
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
		currentSongMenu.textProperty().bind(properties.titleProperty());
		currentSongMenu.disableProperty().bind(properties.mediaSelectedProperty().not());
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

    public void show() {
		stage.show();
    }
}
