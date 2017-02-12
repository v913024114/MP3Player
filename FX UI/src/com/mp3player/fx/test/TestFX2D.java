package com.mp3player.fx.test;

import com.aquafx_project.AquaFx;
import com.mp3player.fx.FileDropOverlay;
import com.mp3player.fx.PlayerControl;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TestFX2D extends Application {
	private Scene scene;


	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("mp3player.fxml"));
		loader.setController(this);
		BorderPane root = loader.load();
		root.setCenter(new PlayerControl());

		new FileDropOverlay(root);
		primaryStage.setScene(scene = new Scene(root));
		scene.getStylesheets().add(getClass().getResource("defaultstyle.css").toExternalForm());
		primaryStage.show();

		Slider realSlider = new Slider(0, 1, 50);
		realSlider.setShowTickMarks(true);


//		new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(slider.valueProperty(), 0)),
//				new KeyFrame(new Duration(10000), new KeyValue(slider.valueProperty(), 99))).play();

	}

	public static void main(String[] args) {
		launch(args);
	}

	@FXML
	public void setStyle(ActionEvent e) {
		MenuItem item = (MenuItem) e.getTarget();
		String text = item.getText();
		if(text.equals("AquaFX")) {
			AquaFx.style();
		} else {
			setUserAgentStylesheet(text.toUpperCase());
		}
	}
}
