package com.mp3player.fx.app;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.aquafx_project.AquaFx;
import com.mp3player.fx.FileDropOverlay;
import com.mp3player.fx.PlayerControl;
import com.mp3player.vdp.RemoteFile;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import mp3player.player.PlayerStatus;
import mp3player.player.data.Media;

public class PlayerWindow implements Initializable {
	private Scene scene;
	private Stage stage;
	private StackPane root;

	@FXML private Menu currentSongMenu, settingsMenu;
	@FXML private MenuBar menuBar;
	@FXML private Slider volume;
	@FXML private ListView<Media> playlist;
	@FXML private TextField searchField;
	@FXML private Button removeOthersButton;
	private Pane playlistRoot, searchRoot;

	private PlayerControl control;

	private PlayerStatus status;
	private PlayerStatusWrapper properties;


	public PlayerWindow(PlayerStatus status, Stage stage) throws IOException {
		this.stage = stage;
		this.status = status;
		properties = new PlayerStatusWrapper(status);

		root = new StackPane();
		root.getChildren().add(loadPlayer());
		playlistRoot = loadPlaylist();
		searchRoot = loadSearch();

		FileDropOverlay overlay = new FileDropOverlay(root);
		overlay.setActionGenerator(files -> generateDropButtons(files));

		stage.setScene(scene = new Scene(root));
		scene.getStylesheets().add(getClass().getResource("defaultstyle.css").toExternalForm());

		stage.setTitle("MX Player");
		stage.getIcons().add(new Image(getClass().getResource("window-icon.png").toExternalForm()));

		stage.setOnHidden(e -> {
			quit();
		});
	}

	private BorderPane loadPlayer() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("mp3player.fxml"));
		loader.setController(this);
		BorderPane playerRoot = loader.load();

		control = new PlayerControl();
		control.durationProperty().bind(properties.durationProperty());
		control.positionProperty().bindBidirectional(properties.positionProperty());
		control.playingProperty().bindBidirectional(properties.playingProperty());
		control.mediaSelectedProperty().bind(properties.mediaSelectedProperty());
		control.playlistAvailableProperty().bind(properties.playlistAvailableProperty());
		control.shuffledProperty().bindBidirectional(properties.shuffledProperty());
		control.loopProperty().bindBidirectional(properties.loopProperty());
		control.setOnNext(e -> properties.getStatus().next());
		control.setOnPrevious(e -> properties.getStatus().previous());
		control.setOnStop(e -> properties.stop());
		control.setOnShowPlaylist(e -> showPlaylist());
		control.setOnSearch(e -> showSearch());
		playerRoot.setCenter(control);
		return playerRoot;
	}

	private BorderPane loadPlaylist() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("playlist.fxml"));
		loader.setController(this);
		BorderPane playlistRoot = loader.load();
		playlistRoot.setBackground(new Background(new BackgroundFill(new Color(0,0,0,0.5), CornerRadii.EMPTY, Insets.EMPTY)));
		return playlistRoot;
	}

	private BorderPane loadSearch() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("search.fxml"));
		loader.setController(this);
		BorderPane searchRoot = loader.load();
		searchRoot.setBackground(new Background(new BackgroundFill(new Color(0,0,0,0.5), CornerRadii.EMPTY, Insets.EMPTY)));
		return searchRoot;
	}


	public PlayerStatusWrapper getStatusWrapper() {
		return properties;
	}

	private List<ToggleButton> generateDropButtons(List<File> files) {
		List<ToggleButton> result = new ArrayList<>(4);

		List<File> audioFiles = AudioFiles.trim(files);
		boolean cold = status.getPlaylist().isEmpty();

		// Play / New Playlist
		if(!audioFiles.isEmpty()) {
			ToggleButton play = new ToggleButton("Play", loadIcon("../icons/Play_MouseOn.png", 32));
			play.setOnAction(e -> play(audioFiles, files.get(0)));
			result.add(play);
		}

		// Add to Playlist
		if(!cold && !audioFiles.isEmpty()) {
			ToggleButton append = new ToggleButton("Add to playlist", loadIcon("../icons/Append_MouseOn.png", 32));
			append.setOnAction(e -> {
				List<RemoteFile> remoteFiles = audioFiles.stream().map(file -> status.getVdp().mountFile(file)).collect(Collectors.toList());
				Media mediaID = status.getPlaylist().addAll(remoteFiles, 0, status.getTarget().isShuffled());
				if(status.getPlayback().getCurrentMedia() == null) {
					status.getTarget().setTargetMedia(mediaID, true);
				}
			});
			result.add(append);
		}

		// Play Folder
		if(files.size() == 1) {
			File file = files.get(0);
			List<File> allAudioFiles = AudioFiles.allAudioFilesIn(files.get(0).getParentFile());
			if(allAudioFiles.size() > 1) {
				ToggleButton playFolder = new ToggleButton("Play folder", loadIcon("../icons/Shuffle_MouseOn.png", 32));
				playFolder.setOnAction(e -> play(allAudioFiles, AudioFiles.isAudioFile(file) ? file : allAudioFiles.get(0)));
				result.add(playFolder);
			}
		}


		return result;
	}

	private void play(List<File> localFiles, File startFile) {
		int startIndex = localFiles.indexOf(startFile);
		List<RemoteFile> remoteFiles = localFiles.stream().map(file -> status.getVdp().mountFile(file)).collect(Collectors.toList());
		Media mediaID = status.getPlaylist().setAll(remoteFiles, startIndex, status.getTarget().isShuffled());
		status.getTarget().setTargetMedia(mediaID, true);
	}

	private void fadeIn(Node node) {
		Duration time = new Duration(200);
		root.getChildren().add(node);

		TranslateTransition in = new TranslateTransition(time, node);
		in.setFromY(-40);
		in.setToY(0);
		in.play();

		FadeTransition fade = new FadeTransition(time, node);
		fade.setFromValue(0);
		fade.setToValue(1);
		fade.play();
	}

	private void fadeOut(Node node) {
		Duration time = new Duration(200);

		TranslateTransition in = new TranslateTransition(time, node);
		in.setFromY(0);
		in.setToY(-20);
		in.play();

    	FadeTransition fade = new FadeTransition(time, node);
		fade.setFromValue(1);
		fade.setToValue(0);
		fade.play();
		fade.setOnFinished(e -> root.getChildren().remove(node));
	}

	public void showPlaylist() {
		fadeIn(playlistRoot);
	}
    @FXML
    public void closePlaylist() {
    	fadeOut(playlistRoot);
    }

    @FXML
	public void showSearch() {
		fadeIn(searchRoot);
		searchField.requestFocus();
	}

	@FXML
	public void closeSearch() {
		fadeOut(searchRoot);
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
		if(playlist == null) {
			// Initialize UI
			settingsMenu.setText(null);
			settingsMenu.setGraphic(loadIcon("settings.png", 20));
			currentSongMenu.setGraphic(loadIcon("file.png", 20));
			currentSongMenu.textProperty().bind(properties.titleProperty());
			currentSongMenu.disableProperty().bind(properties.mediaSelectedProperty().not());
			volume.valueProperty().bindBidirectional(properties.gainProperty());
		}
		else if(searchField == null) {
			// Initialize playlist view
			playlist.setItems(properties.getPlaylist());
			removeOthersButton.disableProperty().bind(properties.playlistAvailableProperty().not());
		}
		else {
			// Initialize search view
		}
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

    @FXML
    public void quit() {
    	System.exit(0);
    }

    @FXML
    public void clearPlaylist() {
    	status.getTarget().setTargetMedia(Optional.empty(), false);
    	status.getPlaylist().clear();
    }

    @FXML
    public void clearOthers() {
    	List<Media> newList = new ArrayList<>();
    	properties.getStatus().getPlayback().getCurrentMedia().ifPresent(m -> newList.add(m));
    	properties.getStatus().getPlaylist().setAll(newList);
    }
}
