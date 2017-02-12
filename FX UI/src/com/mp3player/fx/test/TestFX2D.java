package com.mp3player.fx.test;

import com.aquafx_project.AquaFx;
import com.mp3player.fx.CircularSlider;
import com.mp3player.fx.FileDropOverlay;
import com.mp3player.fx.PlayerControl;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TestFX2D extends Application {
	private Scene scene;


	@Override
	public void start(Stage primaryStage) throws Exception {
//		CircularSlider slider = new CircularSlider();

		BorderPane root = new BorderPane(new PlayerControl());
		new FileDropOverlay(root);
		primaryStage.setScene(scene = new Scene(root));
		scene.getStylesheets().add(getClass().getResource("defaultstyle.css").toExternalForm());
		primaryStage.show();

		Slider realSlider = new Slider(0, 1, 50);
		realSlider.setShowTickMarks(true);
		root.setBottom(realSlider);


//		new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(slider.valueProperty(), 0)),
//				new KeyFrame(new Duration(10000), new KeyValue(slider.valueProperty(), 99))).play();

//		slider.setDuration(3.4*60);


		new Thread(() -> {
			try {
				Thread.sleep(4000);
				Platform.runLater(() -> {
//					setUserAgentStylesheet(STYLESHEET_CASPIAN);
//					AquaFx.style();
//					scene.getStylesheets().add(getClass().getResource("dark.css").toExternalForm());
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
