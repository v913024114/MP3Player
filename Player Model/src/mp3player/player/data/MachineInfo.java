package mp3player.player.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mp3player.vdp.Conflict;
import com.mp3player.vdp.Distributed;
import com.mp3player.vdp.Peer;

public class MachineInfo extends Distributed {
	private static final long serialVersionUID = 8986346914415798967L;


	private List<Speaker> speakers;
	// TODO library


	public static String id(Peer peer) {
		return "machineinfo-"+peer.getID();
	}


	public MachineInfo(Peer peer, List<Speaker> speakers) {
		super(id(peer), false, true);
		this.speakers = new ArrayList<>(speakers);
	}

	@Override
	public Distributed resolveConflict(Conflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Speaker> getSpeakers() {
		return Collections.unmodifiableList(speakers);
	}
}
