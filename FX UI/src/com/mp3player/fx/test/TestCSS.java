package com.mp3player.fx.test;
import com.sun.javafx.scene.control.skin.SliderSkin;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class TestCSS extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	/*
	 * Button blue outline: -fx-focus-color
	 * Progress bar color: .progress-bar > .bar { -fx-background-color:-fx-accent ...
	 */

	@Override
	public void start(Stage primaryStage) throws Exception {
		Region bg = new Region();
		bg.setVisible(false);
		bg.setManaged(false);
		bg.setStyle("-fx-background-color:"+
        "red,"+
        "green;"+
    "-fx-background-insets: 1, 2;"+
    "-fx-padding: 0.416667em;");

		primaryStage.setScene(new Scene(new BorderPane(bg)));
		primaryStage.show();

		System.out.println(bg.getBackground().getFills());

		Slider slider;
		SliderSkin skin;
		Axis<?> axis;
		ValueAxis va;
		NumberAxis na;

	}
}
