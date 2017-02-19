package com.mp3player.fx.test;

import com.mp3player.fx.app.PlayerStatusWrapper;
import com.mp3player.fx.app.PlayerWindow;
import com.mp3player.playback.PlaybackEngine;
import com.mp3player.vdp.VDP;

import javafx.application.Application;
import javafx.stage.Stage;
import mp3player.machine.LocalMachine;
import mp3player.mediacommand.CombinationManager;
import mp3player.mediacommand.MediaCommand;
import mp3player.mediacommand.MediaCommandManager;
import mp3player.player.PlayerStatus;

public class TestFX2D extends Application {


	@Override
	public void start(Stage primaryStage) throws Exception {
		VDP vdp = new VDP();

		PlayerStatus status = new PlayerStatus(vdp);
		PlayerWindow window = new PlayerWindow(status, primaryStage);
		window.show();

		addControl(window.getStatusWrapper());
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


	public static void addControl(PlayerStatusWrapper player) {
		if(MediaCommandManager.isSupported()) {
        	MediaCommandManager manager = MediaCommandManager.getInstance();
        	CombinationManager cm = new CombinationManager();
        	cm.register(manager);

        	cm.addCombination(new MediaCommand[]{ MediaCommand.PLAY_PAUSE }, c -> {
        		player.setPlaying(!player.isPlaying());
        	});
        	cm.addCombination(new MediaCommand[]{ MediaCommand.STOP }, c -> player.stop() );
        	cm.addCombination(new MediaCommand[]{ MediaCommand.NEXT }, c -> player.getStatus().next() );
        	cm.addCombination(new MediaCommand[]{ MediaCommand.PREVIOUS }, c -> player.getStatus().previous() );

        	MediaCommand[] playCombination = new MediaCommand[]{ MediaCommand.VOLUME_UP, MediaCommand.VOLUME_DOWN};
        	MediaCommand[] monitorOffCombination = new MediaCommand[]{ MediaCommand.VOLUME_DOWN, MediaCommand.VOLUME_UP};
        	MediaCommand[] nextCombination = new MediaCommand[]{ MediaCommand.VOLUME_UP, MediaCommand.VOLUME_UP, MediaCommand.VOLUME_DOWN, MediaCommand.VOLUME_DOWN};
        	MediaCommand[] previousCombination = new MediaCommand[]{ MediaCommand.VOLUME_DOWN, MediaCommand.VOLUME_DOWN, MediaCommand.VOLUME_UP, MediaCommand.VOLUME_UP};
        	MediaCommand[] deleteCombination = new MediaCommand[]{ MediaCommand.VOLUME_DOWN, MediaCommand.MUTE, MediaCommand.MUTE, MediaCommand.VOLUME_UP };

        	cm.addCombination(playCombination, c -> {
        		player.setPlaying(!player.isPlaying());
        	});
        	cm.addCombination(monitorOffCombination, c -> {
        		LocalMachine machine = LocalMachine.getLocalMachine();
        		if(machine != null) machine.turnOffMonitors();
        	});
        	cm.addCombination(nextCombination, c -> player.getStatus().next());
        	cm.addCombination(previousCombination, c -> player.getStatus().previous());
        	cm.addCombination(deleteCombination, c -> System.out.println("Delete not implemented yet"));
        }
	}

}
