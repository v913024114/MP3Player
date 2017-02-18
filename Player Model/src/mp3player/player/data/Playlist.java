package mp3player.player.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

	private List<String> idList = new ArrayList<>();
	private Map<String, String> mediaToPeer = new HashMap<>();
	private Map<String, String> mediaToPath = new HashMap<>();

	public Playlist() {
		super(VDP_ID, true, false);
	}

	@Override
	public Distributed resolveConflict(Conflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getIDList() {
		return idList;
	}

	public int size() {
		return idList.size();
	}

	public String getPeerID(String mediaID) throws IllegalArgumentException {
		String result = mediaToPeer.get(mediaID);
		if (result == null)
			throw new IllegalArgumentException("media ID " + mediaID + " is not contained in playlist.");
		return result;
	}

	public String getPath(String mediaID) throws IllegalArgumentException {
		String result = mediaToPath.get(mediaID);
		if (result == null)
			throw new IllegalArgumentException("media ID " + mediaID + " is not contained in playlist.");
		return result;
	}

	public Optional<String> first() {
		if(isEmpty()) return Optional.empty();
		return Optional.of(idList.get(0));
	}

	public Optional<String> last() {
		if(isEmpty()) return Optional.empty();
		return Optional.of(idList.get(idList.size()-1));
	}

	public Optional<String> getNext(Optional<String> mediaID, boolean loop) throws IllegalArgumentException {
		if(isEmpty()) return Optional.empty();
		if(!mediaID.isPresent()) return first();

		int index = idList.indexOf(mediaID.get());
		if (index < 0)
			throw new IllegalArgumentException("media ID " + mediaID.get() + " is not contained in playlist.");
		if (index < size() - 1)
			return Optional.of(idList.get(index + 1));
		else if (loop)
			return first();
		else
			return Optional.empty();
	}

	public Optional<String> getPrevious(Optional<String> mediaID, boolean loop) throws IllegalArgumentException {
		if(isEmpty()) return Optional.empty();
		if(!mediaID.isPresent()) return first();

		int index = idList.indexOf(mediaID.get());
		if (index < 0)
			throw new IllegalArgumentException("media ID " + mediaID.get() + " is not contained in playlist.");
		if (index > 0)
			return Optional.of(idList.get(index + -1));
		else if (loop)
			return last();
		else
			return Optional.empty();
	}

	public List<String> setAll(List<RemoteFile> files) {
		_clear();
		return addAll(files);
	}

	public List<String> addAll(List<RemoteFile> files) {
		List<String> ids = files.stream().map(file -> _add(file)).collect(Collectors.toList());
		fireChangedLocally();
		return ids;
	}

	private String _add(RemoteFile file) {
		String id = UUID.randomUUID().toString();
		idList.add(id);
		mediaToPeer.put(id, file.getPeer().getID());
		mediaToPath.put(id, file.getPath());
		return id;
	}

	public String add(RemoteFile file) {
		String id = _add(file);
		fireChangedLocally();
		return id;
	}

	private void _clear() {
		idList.clear();
		mediaToPeer.clear();
		mediaToPath.clear();
	}

	public void clear() {
		_clear();
		fireChangedLocally();
	}

	public boolean isEmpty() {
		return idList.isEmpty();
	}

}
