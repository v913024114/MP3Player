package com.mp3player.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.mp3player.player.status.PlaybackStatus;
import com.mp3player.vdp.Distributed;
import com.mp3player.vdp.RemoteFile;
import com.mp3player.vdp.VDP;

public class MediaIndex {
	private VDP vdp;
	private List<RemoteFile> allRoots;
	private List<RemoteFile> localRoots;
	private MediaSet recentlyUsed;
	private MediaSet localIndex; // no directories directly contained

	private Map<RemoteFile, MediaInfo> infoMap;

	private List<MediaIndexListener> listeners = new CopyOnWriteArrayList<>();



	public MediaIndex(VDP vdp) {
		this.vdp = vdp;
		allRoots = new ArrayList<>();
		localRoots = new ArrayList<>();
		localRoots.add(vdp.mountFile(new File(System.getProperty("user.home"), "Music")));
		recentlyUsed = new MediaSet();
		recentlyUsed.setWorking(false);
		localIndex = new MediaSet();

		infoMap = new HashMap<>();

		Optional<Distributed> playback = vdp.getData(PlaybackStatus.VDP_ID);
		if(!playback.isPresent()) throw new IllegalStateException("playback must be present in VDP");
		playback.get().addDataChangeListener(e -> ((PlaybackStatus)e.getData()).getCurrentMedia().ifPresent(m -> addToRecentlyUsed(m)));

		new Thread(() -> buildLocal()).start();
	}


	public MediaInfo getInfo(RemoteFile file) {
		if(file.isDirectory()) throw new IllegalArgumentException("directory not allowed");
		MediaInfo existing = infoMap.get(file);
		if(existing != null) return existing;
		else {
			System.out.println("Creating new for "+file);
			existing = new MediaInfo(file);
			infoMap.put(file, existing);
			return existing;
		}
	}

	public MediaSet startSearch(MediaFilter filter) {
		MediaSet set = new MediaSet();
		new Thread(() -> {
			List<MediaInfo> searchList = localIndex.getItems().stream().filter(filter).collect(Collectors.toList());
			set.add(searchList);
			set.setWorking(false);
		}).start();
		return set;
	}

	private void addToRecentlyUsed(Identifier id) {
		Optional<MediaInfo> opMedia = id.lookup(vdp).map(file -> getInfo(file));
		if(!opMedia.isPresent()) return;
		MediaInfo media = opMedia.get();
		recentlyUsed.remove(Arrays.asList(media));
		recentlyUsed.add(0, Arrays.asList(media));
	}

	private void buildLocal() {
		localIndex.setWorking(true);
		for(RemoteFile root : localRoots) {
			addToIndex(root);
		}
		localIndex.setWorking(false);
	}

	private void addToIndex(RemoteFile dir) {
		List<RemoteFile> files;
		try {
			files = dir.list().collect(Collectors.toList());
		} catch (UnsupportedOperationException | IOException e) {
			return;
		}
		List<MediaInfo> mediaList = new ArrayList<>();
		List<RemoteFile> subdirs = new ArrayList<>();

		for(RemoteFile file : files) {
			if(file.isDirectory()) subdirs.add(file);
			else if(AudioFiles.isAudioFile(file.getName())){
				mediaList.add(getInfo(file));
			}
		}

		localIndex.add(mediaList);
		for(RemoteFile subdir : subdirs) {
			addToIndex(subdir);
		}
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
