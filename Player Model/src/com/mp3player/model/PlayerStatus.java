package com.mp3player.model;

import java.io.IOException;
import java.util.Optional;

import com.mp3player.player.data.MachineInfo;
import com.mp3player.player.data.Media;
import com.mp3player.player.data.PlaybackStatus;
import com.mp3player.player.data.PlayerTarget;
import com.mp3player.player.data.Playlist;
import com.mp3player.vdp.Peer;
import com.mp3player.vdp.RemoteFile;
import com.mp3player.vdp.VDP;

public class PlayerStatus {
	private VDP vdp;

	private PlaybackStatus playback;
	private PlayerTarget target;
	private Playlist playlist;


	public PlayerStatus(VDP vdp) {
		this.vdp = vdp;

		playback = vdp.getOrAddData(new PlaybackStatus());
		target = vdp.getOrAddData(new PlayerTarget());
		playlist = vdp.getOrAddData(new Playlist());
	}


	public VDP getVdp() {
		return vdp;
	}


	public PlaybackStatus getPlayback() {
		return playback;
	}


	public PlayerTarget getTarget() {
		return target;
	}

	public Optional<MachineInfo> getInfo(Peer peer) {
		return vdp.getData(MachineInfo.id(peer)).map(data -> (MachineInfo)data);
	}


	public Playlist getPlaylist() {
		return playlist;
	}

	public Optional<Media> getNext() {
		return playlist.getNext(playback.getCurrentMedia(), target.isLoop());
	}
	public void next() {
		target.setTargetMedia(getNext(), true);
	}

	public Optional<Media> getPrevious() {
		return playlist.getPrevious(playback.getCurrentMedia(), target.isLoop());
	}
	public void previous() {
		target.setTargetMedia(getPrevious(), true);
	}


	public Optional<RemoteFile> lookup(Media media) {
		if(media == null) return Optional.empty();
		return Optional.ofNullable(vdp.getPeer(media.getPeerID())).map(peer -> {
			try {
				return peer.getFile(media.getPath());
			} catch (IOException e) {
				return null;
			}
		});
	}

	public Optional<RemoteFile> lookup(Optional<Media> optionalMedia) {
		return optionalMedia.map(media -> lookup(media)).orElse(Optional.empty());
	}

}
