package com.mp3player.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mp3player.player.data.Media;

public class MediaSet {
	private List<Media> list;
	private boolean working;

	private List<MediaSelectionListener> listeners = new CopyOnWriteArrayList<>();


	public MediaSet() {
		list = new ArrayList<>();
		working = true;
	}


	public List<Media> getItems() {
		return Collections.unmodifiableList(list);
	}

	public boolean isWorking() {
		return working;
	}

	public void addMediaSelectionListener(MediaSelectionListener l) {
		listeners.add(l);
	}

	public void removeMediaSelectionListener(MediaSelectionListener l) {
		listeners.remove(l);
	}

	void setWorking(boolean working) {
		this.working = working;
		MediaSetEvent e = new MediaSetEvent(this, null);
		listeners.forEach(l -> l.onWorkingChanged(e));
	}

	void add(int index, List<Media> newList) {
		list.addAll(index, newList);
		MediaSetEvent e = new MediaSetEvent(this, Collections.unmodifiableList(newList));
		listeners.forEach(l -> l.onAdded(e));
	}

	// lots of boilerplate objects
	void add(List<Media> newList) {
		list.addAll(newList);
		MediaSetEvent e = new MediaSetEvent(this, Collections.unmodifiableList(newList));
		listeners.forEach(l -> l.onAdded(e));
	}

	void remove(List<Media> newList) {
		if(list.removeAll(newList)) {
			MediaSetEvent e = new MediaSetEvent(this, Collections.unmodifiableList(newList));
			listeners.forEach(l -> l.onRemoved(e));
		}
	}
}
