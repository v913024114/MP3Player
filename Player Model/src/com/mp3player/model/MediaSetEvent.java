package com.mp3player.model;

import java.util.List;

import com.mp3player.player.data.Media;

public class MediaSetEvent {
	private MediaSet set;
	private List<Media> sublist;

	public MediaSetEvent(MediaSet set, List<Media> sublist) {
		this.set = set;
		this.sublist = sublist;
	}

	public MediaSet getSet() {
		return set;
	}

	public List<Media> getSublist() {
		return sublist;
	}

}
