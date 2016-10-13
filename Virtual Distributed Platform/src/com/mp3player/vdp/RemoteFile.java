package com.mp3player.vdp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import com.mp3player.vdp.internal.LocalPeer;

public abstract class RemoteFile {
	private Peer client;


	public RemoteFile(Peer client) {
		this.client = client;
	}


	public Peer getClient() {
		return client;
	}


	public abstract String getName();

	public abstract String getAbsolutePath();

	public abstract String getPath();

	public abstract Optional<RemoteFile> getParentFile();

	public abstract boolean isDirectory();

	public abstract boolean isFile();

	public abstract boolean exists() throws IOException;

	public abstract long lastModified();

	public abstract long length();

	public abstract Stream<RemoteFile> list() throws UnsupportedOperationException, IOException;

	public abstract void copyTo(File localFile) throws IOException;

	public abstract InputStream openStream() throws IOException;

	/**
	 * If this file is stored on the local machine, this method returns
	 * the associated {@link File}.
	 * To test if a file is local, use {@link LocalPeer#isLocal()} on the associated
	 * client.
	 * @return the associated File if available
	 * @throws NoSuchElementException if this file is stored on a remote machine
	 */
	public abstract File localFile() throws NoSuchElementException;
}
