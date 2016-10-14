package com.mp3player.vdp;

import java.util.List;

/**
 * A virtual distributed network ({@link VDP}) consists of equals
 * <code>Peer</code>s.
 * <p>
 * Each peer has a public name and address which can be obtained using
 * {@link #getName()} and {@link #getAddress()}, respectively. These are not
 * used to uniquely identify the peer but rather to serve as information to
 * display to the user.
 * </p>
 * <p>
 * The local machine is also represented as a peer and can be obtained through
 * {@link VDP#getLocalPeer()}. Use {@link Peer#isLocal()} to check for the local
 * peer.
 * </p>
 * <p>
 * Peers can mount local files to make them accessible to other peers using one
 * of {@link VDP}'s mount methods. To access a remotely mounted file, call
 * {@link #getFile(String)} or {@link #getRootFiles()} of that
 * <code>Peer</code>.
 * </p>
 *
 * @author Philipp Holl
 *
 */
public interface Peer {

	/**
	 * Returns a list of all files mounted directly by the peer. Files can be
	 * mounted using one of {@link VDP}'s mount methods.
	 *
	 * @return a list of all files mounted by the peer
	 * @see #getFile(String)
	 */
	List<RemoteFile> getRootFiles();

	/**
	 * Returns the file mounted at the given relative path. For more on
	 * mounting, see {@link RemoteFile}.
	 *
	 * @param path
	 *            mounted file path
	 * @return the file mounted at the given relative path
	 * @see #getRootFiles()
	 */
	RemoteFile getFile(String path);

	/**
	 * Tests if this peer is the local peer. If this is the case, all files
	 * obtained through this peer (excluding virtual files) are associated with
	 * a local file, see {@link RemoteFile#localFile()}.
	 *
	 * @return true if this is the local peer
	 */
	boolean isLocal();

	/**
	 * Returns the name of the peer as human readable text. This may for example
	 * be the name of the computer in the network or a user entered string.
	 *
	 * @return name of the peer
	 */
	String getName();

	/**
	 * Returns a human readable address (e.g. IP address) of the peer. The
	 * address need not be unique.
	 *
	 * @return address of the peer as text
	 */
	String getAddress();

}