package mp3player.player.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mp3player.vdp.Conflict;
import com.mp3player.vdp.Distributed;
import com.mp3player.vdp.RemoteFile;

public class Playlist extends Distributed {
	private static final long serialVersionUID = 7881884218273201562L;

	public static final String VDP_ID = "playlist";

	private List<String> idList = new ArrayList<>();
	private Map<String, String> mediaToPeer = new HashMap<>();
	private Map<String, String> mediaToPath = new HashMap<>();


	public Playlist() {
		super(VDP_ID);
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

	public String getPeerID(String media) {
		String result = mediaToPeer.get(media);
		if(result == null) throw new IllegalArgumentException("media ID "+media+" is not contained in playlist.");
		return result;
	}

	public String getPath(String media) {
		String result = mediaToPath.get(media);
		if(result == null) throw new IllegalArgumentException("media ID "+media+" is not contained in playlist.");
		return result;
	}


	public void set(List<RemoteFile> files) {
		_clear();
		addAll(files);
	}

	public void addAll(List<RemoteFile> files) {
		for(RemoteFile file : files) {
			_add(file);
		}
		fireChangedLocally();
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

}
