package com.mp3player.fx.test;

import com.mp3player.fx.app.PlayerWindow;
import com.mp3player.playback.PlaybackEngine;
import com.mp3player.vdp.VDP;

import javafx.application.Application;
import javafx.stage.Stage;
import mp3player.player.PlayerStatus;

public class TestFX2D extends Application {


	@Override
	public void start(Stage primaryStage) throws Exception {
		VDP vdp = new VDP();

		PlayerStatus status = new PlayerStatus(vdp);
		new PlayerWindow(status, primaryStage).show();;
//		new PlayerWindow(new Stage());

//		new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(slider.valueProperty(), 0)),
//				new KeyFrame(new Duration(10000), new KeyValue(slider.valueProperty(), 99))).play();

		new PlaybackEngine(status);

//		RemoteFile file = vdp.mountFile(new File("C:/stereo.mp3"));
//		String mediaID = status.getPlaylist().add(file);
//
//		status.getTarget().setTargetMedia(mediaID);
//		status.getTarget().setTargetPlaying(true);
	}

	public static void main(String[] args) {
		launch(args);
	}



}
