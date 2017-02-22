package com.mp3player.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mp3player.player.data.Media;
import com.mp3player.player.data.PlaybackStatus;
import com.mp3player.vdp.Distributed;
import com.mp3player.vdp.RemoteFile;
import com.mp3player.vdp.VDP;

public class MediaIndex {
	private VDP vdp;
	private List<RemoteFile> allRoots;
	private List<RemoteFile> localRoots;
	private MediaSet recentlyUsed;

	private List<MediaIndexListener> listeners = new CopyOnWriteArrayList<>();



	public MediaIndex(VDP vdp) {
		this.vdp = vdp;
		allRoots = new ArrayList<>();
		localRoots = new ArrayList<>();
		recentlyUsed = new MediaSet();
		recentlyUsed.setWorking(false);

		Optional<Distributed> playback = vdp.getData(PlaybackStatus.VDP_ID);
		if(!playback.isPresent()) throw new IllegalStateException("playback must be present in VDP");
		playback.get().addDataChangeListener(e -> ((PlaybackStatus)e.getData()).getCurrentMedia().ifPresent(m -> addToRecentlyUsed(m)));
	}


	public Media get(RemoteFile file) {
		return null; // TODO
	}

	public MediaSet startSearch(MediaFilter filter) {
		return null; // TODO
	}

	private void addToRecentlyUsed(Media media) {
		recentlyUsed.remove(Arrays.asList(media));
		recentlyUsed.add(0, Arrays.asList(media));
	}



	public VDP getVdp() {
		return vdp;
	}

	public List<RemoteFile> listAllRoots() {
		return Collections.unmodifiableList(allRoots);
	}

	public List<RemoteFile> localRoots() {
		return Collections.unmodifiableList(localRoots);
	}

	public MediaSet getRecentlyUsed() {
		return recentlyUsed;
	}

	public void addLocalRoot(RemoteFile file) {
		localRoots.add(file);
		allRoots.add(file);

		MediaIndexEvent e = new MediaIndexEvent(this, file);
		listeners.forEach(l -> l.onAdded(e));
	}

	public void removeLocalRoot(RemoteFile file) {
		if(localRoots.remove(file)) {
			allRoots.remove(file);

			MediaIndexEvent e = new MediaIndexEvent(this, file);
			listeners.forEach(l -> l.onRemoved(e));
		}
	}

	public void addMediaIndexListener(MediaIndexListener l) {
		listeners.add(l);
	}

	public void removeMediaIndexListener(MediaIndexListener l) {
		listeners.remove(l);
	}


}
