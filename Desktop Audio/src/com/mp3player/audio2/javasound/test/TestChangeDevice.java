package com.mp3player.audio2.javasound.test;

import java.io.File;
import java.io.IOException;

import com.mp3player.audio2.javasound.JSPlayer;
import com.mp3player.audio2.javasound.JavaSoundEngine;
import com.mp3player.desktopaudio.AudioDevice;
import com.mp3player.desktopaudio.AudioEngineException;
import com.mp3player.desktopaudio.LocalMediaFile;
import com.mp3player.desktopaudio.MediaFile;
import com.mp3player.desktopaudio.UnsupportedMediaFormatException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestChangeDevice extends Application
{
	private JavaSoundEngine engine;
	private JSPlayer player;


	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(getClass().getName());

        startPlayer();

        ComboBox<AudioDevice> box = new ComboBox<AudioDevice>(FXCollections.observableArrayList(engine.getDevices()));
        box.getSelectionModel().select(player.getDevice());
        box.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<AudioDevice>() {
			@Override
			public void changed(ObservableValue<? extends AudioDevice> arg0,
					AudioDevice arg1, AudioDevice arg2) {
				System.out.println("Changine device to "+arg2);
				try {
					player.switchDevice(arg2);
				} catch (AudioEngineException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

        StackPane root = new StackPane();
        root.getChildren().add(box);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

	public void startPlayer() {
		// Create MediaFile
		MediaFile media = new LocalMediaFile(new File("C:/stereo.mp3"));

		// Initialize AudioEngine
		try {
			engine = new JavaSoundEngine();
		} catch (AudioEngineException e1) {
			e1.printStackTrace();
			return;
		}


		// Test
		player = (JSPlayer) engine.newPlayer(media);
		try {
			long time = System.currentTimeMillis();
			player.prepare();
			System.out.println("Time to prepare: "+(System.currentTimeMillis()-time));
			player.activate(engine.getDefaultDevice());
		} catch (UnsupportedMediaFormatException | IOException
				| AudioEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Starting player...");
		player.start();
		System.out.println("Player started.");
	}

}
