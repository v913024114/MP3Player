package mp3player.player;

import java.io.IOException;
import java.util.Optional;

import com.mp3player.vdp.RemoteFile;
import com.mp3player.vdp.VDP;

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


	public Optional<RemoteFile> lookup(String mediaID) {
		if(mediaID == null) return Optional.empty();
		return Optional.ofNullable(vdp.getPeer(playlist.getPeerID(mediaID))).map(peer -> {
			try {
				return peer.getFile(playlist.getPath(mediaID));
			} catch (IOException e) {
				return null;
			}
		});
	}

	public Optional<RemoteFile> lookup(Optional<String> optionalMediaID) {
		if(!optionalMediaID.isPresent()) return Optional.empty();
		else return lookup(optionalMediaID.get());
	}


}
