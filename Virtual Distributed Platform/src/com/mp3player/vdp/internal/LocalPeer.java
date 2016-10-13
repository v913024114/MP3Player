package com.mp3player.vdp.internal;

import java.util.List;
import java.util.Optional;

import com.mp3player.vdp.Peer;
import com.mp3player.vdp.RemoteFile;
import com.mp3player.vdp.VDP;

public class LocalPeer implements Peer {
	private String id;
	private String name;
	private String address; // IP address only for display purposes

	private VDP vdp;
	private Optional<LocalPeer> proxy; // if client cannot be reached directly
	private Optional<Connection> connection; // only for directly connected clients


	LocalPeer(VDP vdp) {

	}


	@Override
	public List<RemoteFile> getRootFiles() {
		if(isLocal()) {
			return vdp.getLocalRoots();
		} else {

		}
	}


	@Override
	public RemoteFile getFile(String path) {
		if(isLocal()) {
			return vdp.getLocalFile(path);
		} else {

		}
	}



	@Override
	public boolean isLocal() {
		return equals(vdp.getLocalClient());
	}


	@Override
	public String getId() {
		return id;
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public String getAddress() {
		return address;
	}

}
