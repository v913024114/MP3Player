package com.mp3player.playback;

import java.io.IOException;
import java.util.Optional;

import com.mp3player.vdp.DataChangeEvent;
import com.mp3player.vdp.RemoteFile;

import mp3player.audio2.javasound.JavaSoundEngine;
import mp3player.desktopaudio.AudioEngine;
import mp3player.desktopaudio.AudioEngineException;
import mp3player.desktopaudio.LocalMediaFile;
import mp3player.desktopaudio.MediaFile;
import mp3player.desktopaudio.Player;
import mp3player.player.PlayerStatus;
import mp3player.player.data.PlaybackStatus;
import mp3player.player.data.PlayerTarget;

public class PlaybackEngine {
	private PlayerStatus status;
	private PlayerTarget target;
	private PlaybackStatus info;
	private AudioEngine audio;

	private Optional<RemoteFile> currentMedia = Optional.empty(); // locally playing media
	private String currentMediaID;
	private Player player;


	public PlaybackEngine(PlayerStatus status) {
		this.status = status;
		target = status.getTarget();
		info = status.getPlayback();

		try {
			audio = new JavaSoundEngine();
		} catch (AudioEngineException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		status.getTarget().addDataChangeListener(e -> targetChanged(e));
	}


	private void targetChanged(DataChangeEvent e) {
		if(!currentMedia.equals(status.lookup(target.getTargetMedia()))) {
			// load file
			currentMedia = status.lookup(target.getTargetMedia());
			currentMediaID = target.getTargetMedia();
			if(currentMedia.isPresent()) {
				MediaFile file = new LocalMediaFile(currentMedia.get().localFile());
				player = audio.newPlayer(file);
				try {
					player.prepare();
					player.activate(audio.getDefaultDevice());
				} catch(Exception exc) {
					exc.printStackTrace();
				}
			}
		}
		if(target.isTargetPlaying()) {
			player.start();
		} else {
			player.pause();
		}
		if(target.isTargetPositionSet()) {
			try {
				player.setPositionBlocking(target.getTargetPosition(), 1);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		info.setStatus(currentMediaID, player.getGain(), player.getDevice().getMinGain(), player.getDevice().getMaxGain(), player.isMute(), player.isPlaying(), false, null, player.getPosition(), System.currentTimeMillis(), player.getDuration());
	}

}
