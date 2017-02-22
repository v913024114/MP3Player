package com.mp3player.player.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mp3player.vdp.Conflict;
import com.mp3player.vdp.Distributed;
import com.mp3player.vdp.RemoteFile;

/**
 * The playlist contains all currently shared media files with their IDs and
 * respective locations. It resembles the {@link PlayerTarget} in that it
 * defines what the playback engine should play next. However it is not part
 * thereof to reduce network traffic as the playlist is not changed that often.
 *
 * @author Philipp Holl
 *
 */
public class Playlist extends Distributed {
	private static final long serialVersionUID = 7881884218273201562L;

	public static final String VDP_ID = "playlist";

	private List<Media> list = new ArrayList<>();

	public Playlist() {
		super(VDP_ID, true, false);
	}

	@Override
	public Distributed resolveConflict(Conflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Media> list() {
		return new ArrayList<>(list);
	}

	public int size() {
		return list.size();
	}

	public Optional<Media> first() {
		if(isEmpty()) return Optional.empty();
		return Optional.of(list.get(0));
	}

	public Optional<Media> last() {
		if(isEmpty()) return Optional.empty();
		return Optional.of(list.get(size()-1));
	}

	public Optional<Media> getNext(Optional<Media> current, boolean loop) throws IllegalArgumentException {
		if(isEmpty()) return Optional.empty();
		if(!current.isPresent()) return first();

		int index = list.indexOf(current.get());
		if (index < 0)
			throw new IllegalArgumentException("media ID " + current.get() + " is not contained in playlist.");
		if (index < size() - 1)
			return Optional.of(list.get(index + 1));
		else if (loop)
			return first();
		else
			return Optional.empty();
	}

	public Optional<Media> getPrevious(Optional<Media> mediaID, boolean loop) throws IllegalArgumentException {
		if(isEmpty()) return Optional.empty();
		if(!mediaID.isPresent()) return first();

		int index = list.indexOf(mediaID.get());
		if (index < 0)
			throw new IllegalArgumentException("media ID " + mediaID.get() + " is not contained in playlist.");
		if (index > 0)
			return Optional.of(list.get(index + -1));
		else if (loop)
			return last();
		else
			return Optional.empty();
	}

	public Media setAll(List<RemoteFile> files, int returnIDIndex, boolean shuffle, boolean firstStayFirst) {
		_clear();
		List<Media> newList = files.stream().map(file -> _add(file)).collect(Collectors.toList());
		Media returnID = returnIDIndex >= 0 ? newList.get(returnIDIndex) : null;
		if(shuffle) _shuffle(Optional.of(returnID));
		fireChangedLocally();
		return returnID;
	}

	public Media addAll(List<RemoteFile> files, int returnIDIndex, boolean shuffle, Optional<Media> shuffleToFirst) {
		List<Media> newList = files.stream().map(file -> _add(file)).collect(Collectors.toList());
		Media returnID = returnIDIndex >= 0 ? newList.get(returnIDIndex) : null;
		if(shuffle) _shuffle(shuffleToFirst);
		fireChangedLocally();
		return returnID;
	}

	public void shuffle(Optional<Media> makeFirst) {
		_shuffle(makeFirst);
		fireChangedLocally();
	}

	private void _shuffle(Optional<Media> makeFirst) {
		Collections.shuffle(list);
		makeFirst.ifPresent(first -> {
			if(list.remove(first)) list.add(0, first);
			else throw new IllegalArgumentException("makeFirst is not contained in playlist");
		});
	}

	private Media _add(RemoteFile file) {
		Media media = new Media(file.getPeer().getID(), file.getPath());
		list.add(media);
		return media;
	}

	public Media add(RemoteFile file) {
		Media media = _add(file);
		fireChangedLocally();
		return media;
	}

	private void _clear() {
		list.clear();
	}

	public void clear() {
		_clear();
		fireChangedLocally();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public void setAll(List<Media> newList) {
		list.clear();
		list.addAll(newList);
		fireChangedLocally();
	}

}
