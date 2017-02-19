package mp3player.player;

import java.io.IOException;
import java.util.Optional;

import com.mp3player.vdp.RemoteFile;
import com.mp3player.vdp.VDP;

import mp3player.player.data.Media;
import mp3player.player.data.PlaybackStatus;
import mp3player.player.data.PlayerTarget;
import mp3player.player.data.Playlist;

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


	public Playlist getPlaylist() {
		return playlist;
	}

	public Optional<Media> getNext() {
		return playlist.getNext(playback.getCurrentMedia(), target.isLoop());
	}
	public void next() { // TODO nothing happens when next==current
		target.setTargetMedia(getNext(), true);
	}

	public Optional<Media> getPrevious() {
		return playlist.getPrevious(playback.getCurrentMedia(), target.isLoop());
	}
	public void previous() { // TODO nothing happens when previous==current
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
