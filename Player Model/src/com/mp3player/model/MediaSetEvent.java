package com.mp3player.model;

import java.util.List;

public class MediaSetEvent {
	private MediaSet set;
	private List<MediaInfo> sublist;

	public MediaSetEvent(MediaSet set, List<MediaInfo> sublist) {
		this.set = set;
		this.sublist = sublist;
	}

	public MediaSet getSet() {
		return set;
	}

	public List<MediaInfo> getSublist() {
		return sublist;
	}

}
