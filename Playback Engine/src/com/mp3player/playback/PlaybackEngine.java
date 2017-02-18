package com.mp3player.playback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mp3player.vdp.RemoteFile;

import mp3player.audio2.javasound.JavaSoundEngine;
import mp3player.desktopaudio.AudioDevice;
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
	private final List<String> supportedTypes;

	private Optional<RemoteFile> currentMedia = Optional.empty(); // locally playing media
	private Optional<String> currentMediaID = Optional.empty();
	private Player player;
	private long lastPositionUpdate;
	private AudioDevice device;

	private double gain;
	private boolean mute;
	private String errorMessage;


	public PlaybackEngine(PlayerStatus status) throws AudioEngineException {
		this.status = status;
		target = status.getTarget();
		info = status.getPlayback();

		audio = new JavaSoundEngine();
		device = audio.getDefaultDevice();
		supportedTypes = new ArrayList<>(audio.getSupportedMediaTypes().stream().map(t -> t.getFileExtension()).collect(Collectors.toList()));

		status.getTarget().addDataChangeListener(e -> targetChanged());
	}


	public void next() {
		Optional<String> opNext = status.getPlaylist().getNext(currentMediaID, target.isLoop());
		if(!opNext.equals(target.getTargetMedia())) {
			target.setTargetMedia(opNext, true);
		} else {
			target.setTargetPosition(0, true);
		}
	}


	private void targetChanged() {
		if(containsDevice(target.getTargetDevice())) {
			loadFile();
			if(player != null) adjustPlayer();
			publishInfo();
		}
		else if(!audio.getPlayers().isEmpty()){
			audio.getPlayers().forEach(player -> player.dispose());
		}
	}

	private void loadFile() {
		if(!currentMedia.equals(status.lookup(target.getTargetMedia()))) {
			// Change file

			if(player != null) player.dispose();
			player = null;

			publishInfo();

			currentMedia = status.lookup(target.getTargetMedia());
			currentMediaID = target.getTargetMedia();

			if(currentMedia.isPresent()) {
				MediaFile file;
				if(currentMedia.get().getPeer().isLocal()) {
					file = new LocalMediaFile(currentMedia.get().localFile());
				} else {
					// TODO copy to local
					throw new UnsupportedOperationException("file copying not supported yet");
				}
				player = audio.newPlayer(file);
				try {
					player.prepare();
					player.activate(audio.getDefaultDevice());
					player.setMute(mute);
					player.setGain(gain);
					player.addEndOfMediaListener(e -> next());
					errorMessage = null;
				} catch(Exception exc) {
					player = null;
					exc.printStackTrace();
					errorMessage = exc.getMessage();
				}
			}
		}
	}






	private void adjustPlayer() {
		if(mute != target.isTargetMute()) {
			mute = target.isTargetMute();
			player.setMute(mute);
		}
		if(gain != target.getTargetGain()) {
			gain = target.getTargetGain();
			player.setGain(gain);
		}
		if(target.wasTargetPositionSetAfter(lastPositionUpdate)) {
			lastPositionUpdate = target.getPositionUpdateTime();
			try {
				System.out.println("Setting position to "+target.getTargetPosition().getAsDouble()+", issued at "+target.getPositionUpdateTime());
				player.setPositionBlocking(target.getTargetPosition().getAsDouble(), 1.0);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if(target.isTargetPlaying()) {
			player.start();
		} else {
			player.pause();
		}
	}

	public void publishInfo() {
		boolean playing = player != null ? player.isPlaying() : false;
		double position = player != null ? player.getPosition() : 0;
		double duration = player != null ? player.getDuration() : 0;

		info.setStatus(target.getTargetDevice(), supportedTypes, currentMediaID,
				gain, device.getMinGain(), device.getMaxGain(), mute,
				playing, false, errorMessage, position, System.currentTimeMillis(), duration);
	}


	public boolean containsDevice(String deviceID) {
		return true;
	}

}
