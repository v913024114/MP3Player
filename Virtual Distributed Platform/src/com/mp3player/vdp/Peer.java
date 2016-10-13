package com.mp3player.vdp;

import java.util.List;

public interface Peer {

	/**
	 * Returns a list of all files mounted by the peer.
	 * Files can be mounted using {@link VDP#mountFile(java.io.File)}.
	 * @return a list of all files mounted by the peer
	 */
	List<RemoteFile> getRootFiles();

	RemoteFile getFile(String path);

	boolean isLocal();

	/**
	 * Unique peer ID.
	 * @return
	 */
	String getId();

	String getName();

	/**
	 * Human readable address (e.g. IP address) of the peer.
	 * The address need not be unique.
	 * @return
	 */
	String getAddress();

}