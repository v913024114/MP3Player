package com.mp3player.vdp.internal;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mp3player.vdp.Peer;
import com.mp3player.vdp.RemoteFile;

public class LocalPeer implements Peer {
	private String id;
	private String name;
	private String address; // IP address only for display purposes

//	private Optional<LocalPeer> proxy; // if client cannot be reached directly
//	private Optional<Connection> connection; // only for directly connected clients

	private List<RemoteFile> rootFiles;


	public LocalPeer() {
		id = UUID.randomUUID().toString();
		name = "Local"; // TODO
		address = "localhost"; // TODO
		rootFiles = new ArrayList<RemoteFile>();
	}


	@Override
	public List<RemoteFile> getRootFiles() {
		return rootFiles;
	}


	@Override
	public RemoteFile getFile(String path) {
		for(RemoteFile file : rootFiles) {
			if(file.getPath().equals(path)) return file;
		}
		throw new UnsupportedOperationException("not implemented"); // TODO
	}

	public RemoteFile mount(File file) {
		return mount(file.getName(), file); // TODO name may already be taken
	}

	public RemoteFile mount(String name, File file) {
		LocalFile lf = LocalFile.createRoot(this, file, name);
		rootFiles.add(lf);
		return lf;
	}



	@Override
	public boolean isLocal() {
		return true;
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public String getAddress() {
		return address;
	}


	@Override
	public void send(Serializable message) throws IOException {
		// TODO Auto-generated method stub

	}


	@Override
	public String getID() {
		return id;
	}

}
