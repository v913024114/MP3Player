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
import com.mp3player.fx.icons.FXIcons;
import com.mp3player.fx.playerwrapper.MediaIndexWrapper;
import com.mp3player.fx.playerwrapper.PlayerStatusWrapper;
import com.mp3player.model.MediaIndex;
import com.mp3player.model.PlayerStatus;
import com.mp3player.player.data.Media;
import com.mp3player.player.data.Speaker;
import com.mp3player.vdp.RemoteFile;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayerWindow implements Initializable {
	private Scene scene;
	private Stage stage;
	private StackPane root;

	@FXML private Menu currentSongMenu, settingsMenu;
	@FXML private MenuBar menuBar;
	@FXML private Slider volume;
	@FXML private ComboBox<Speaker> speakerSelection;
	@FXML private ListView<Media> playlist, searchResult;
	@FXML private TextField searchField;
	@FXML private Button removeOthersButton;
	private Pane playlistRoot, searchRoot;

	private PlayerControl control;

	private PlayerStatus status;
	private PlayerStatusWrapper properties;
	private MediaIndexWrapper index;


	public PlayerWindow(PlayerStatus status, MediaIndex index, Stage stage) throws IOException {
		this.stage = stage;
		this.status = status;
		properties = new PlayerStatusWrapper(status);
		this.index = new MediaIndexWrapper(index);

		root = new StackPane();
		root.getChildren().add(loadPlayer());
		playlistRoot = loadPlaylist();
		searchRoot = loadSearch();

		FileDropOverlay overlay = new FileDropOverlay(root);
		overlay.setActionGenerator(files -> generateDropButtons(files));

		stage.setScene(scene = new Scene(root));
		scene.getStylesheets().add(getClass().getResource("defaultstyle.css").toExternalForm());

		stage.setTitle("MX Player");
		stage.getIcons().add(FXIcons.get("Play2.png", 32).getImage());

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
			ToggleButton play = new ToggleButton("Play", FXIcons.get("Play2.png", 32));
			play.setOnAction(e -> play(audioFiles, files.get(0)));
			result.add(play);
		}

		// Add to Playlist
		if(!cold && !audioFiles.isEmpty()) {
			ToggleButton append = new ToggleButton("Add to playlist", FXIcons.get("Append.png", 32));
			append.setOnAction(e -> {
				List<RemoteFile> remoteFiles = audioFiles.stream().map(file -> status.getVdp().mountFile(file)).collect(Collectors.toList());
				Media mediaID = status.getPlaylist().addAll(remoteFiles, 0, status.getTarget().isShuffled(), status.getPlayback().getCurrentMedia());
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
				ToggleButton playFolder = new ToggleButton("Play folder", FXIcons.get("PlayFolder.png", 32));
				playFolder.setOnAction(e -> play(allAudioFiles, AudioFiles.isAudioFile(file) ? file : allAudioFiles.get(0)));
				result.add(playFolder);
			}
		}


		return result;
	}

	public void play(List<File> localFiles, File startFile) {
		int startIndex = localFiles.indexOf(startFile);
		List<RemoteFile> remoteFiles = localFiles.stream().map(file -> status.getVdp().mountFile(file)).collect(Collectors.toList());
		Media mediaID = status.getPlaylist().setAll(remoteFiles, startIndex, status.getTarget().isShuffled(), true);
		status.getTarget().setTargetMedia(mediaID, true);
	}

	private void fadeIn(Node node) {
		Duration time = new Duration(200);
		root.getChildren().add(node);

		TranslateTransition in = new TranslateTransition(time, node);
		in.setFromY(-20);
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
		in.setToY(-10);
		in.play();

    	FadeTransition fade = new FadeTransition(time, node);
		fade.setFromValue(1);
		fade.setToValue(0);
		fade.play();
		fade.setOnFinished(e -> root.getChildren().remove(node));
	}

	public void showPlaylist() {
		fadeIn(playlistRoot);
		playlist.requestFocus();
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
			settingsMenu.setGraphic(FXIcons.get("Settings.png", 24));
			currentSongMenu.setGraphic(FXIcons.get("Media.png", 24));
			currentSongMenu.textProperty().bind(properties.titleProperty());
			currentSongMenu.disableProperty().bind(properties.mediaSelectedProperty().not());
			volume.valueProperty().bindBidirectional(properties.gainProperty());
			speakerSelection.setItems(properties.getSpeakers());
			speakerSelection.getSelectionModel().selectedItemProperty().addListener((p,o,n) -> {
				if(n != null) properties.setSpeaker(Optional.of(n));
			});
			properties.speakerProperty().addListener((p,o,n) -> {
				speakerSelection.getSelectionModel().select(n.orElse(null));
			});
		}
		else if(searchField == null) {
			// Initialize playlist view
			playlist.setItems(properties.getPlaylist());
			removeOthersButton.disableProperty().bind(properties.playlistAvailableProperty().not());
			playlist.getSelectionModel().selectedItemProperty().addListener((p,o,n) -> {
				if(n != null) properties.setCurrentMedia(Optional.of(n));
			});
			properties.currentMediaProperty().addListener((p,o,n) -> {
				playlist.getSelectionModel().select(properties.getCurrentMedia().orElse(null));
			});
			playlist.setOnMouseReleased(e -> {
				if(e.getButton() == MouseButton.PRIMARY) {
					Platform.runLater(() -> closePlaylist());
				}
			});
		}
		else {
			// Initialize search view
			searchResult.setItems(index.getRecentlyUsed().getItems());
		}
	}

    public void show() {
		stage.show();
		System.out.println(stage.getWidth()+" x "+stage.getHeight());

		// default values, apply for bundled application
//    	stage.setWidth(314);
//    	stage.setHeight(398);
    }

    @FXML
    public void quit() {
    	System.exit(0);
    }

    @FXML
    public void clearPlaylist() {
    	status.getTarget().setTargetMedia(Optional.empty(), false);
    	status.getPlaylist().clear();
    	closePlaylist();
    }

    @FXML
    public void clearOthers() {
    	List<Media> newList = new ArrayList<>();
    	properties.getStatus().getPlayback().getCurrentMedia().ifPresent(m -> newList.add(m));
    	properties.getStatus().getPlaylist().setAll(newList);
    }

    @FXML
    public void displayInfo() {
    	Alert info = new Alert(AlertType.INFORMATION);
    	info.setTitle("Info");
    	info.setHeaderText("MX Player");
    	info.setContentText("MX Player pre 0.1\nAuthor: Philipp Holl\nFeb 20, 2017");
    	info.initOwner(stage);
    	info.initModality(Modality.NONE);
    	info.show();
    }
}
