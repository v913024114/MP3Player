package com.mp3player.fx.app;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.mp3player.fx.playerwrapper.PlayerStatusWrapper;
import com.mp3player.machine.LocalMachine;
import com.mp3player.mediacommand.CombinationManager;
import com.mp3player.mediacommand.MediaCommand;
import com.mp3player.mediacommand.MediaCommandManager;
import com.mp3player.model.MediaIndex;
import com.mp3player.playback.PlaybackEngine;
import com.mp3player.player.status.PlayerStatus;
import com.mp3player.vdp.VDP;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {


	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("Parameters: "+getParameters().getUnnamed());

		VDP vdp = new VDP();

		PlayerStatus status = new PlayerStatus(vdp);
		MediaIndex index = new MediaIndex(vdp);

		PlayerWindow window = new PlayerWindow(status, index, primaryStage);
		window.show();

		addControl(window.getStatusWrapper());

		new PlaybackEngine(status);

		List<File> files = getParameters().getUnnamed().stream().map(path -> new File(path)).filter(file -> file.exists()).collect(Collectors.toList());
		if(!files.isEmpty()) {
			window.play(files, files.get(0));
		}
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
