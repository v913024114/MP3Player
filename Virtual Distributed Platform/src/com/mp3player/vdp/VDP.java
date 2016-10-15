package com.mp3player.vdp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Main class for setting up a virtual distributed platform.
 *
 */
public class VDP {
	private Peer localPeer;

	private Consumer<ConnectionEvent> onPeerConnected, onPeerDisconnected;
	private Consumer<Distributed> onDataAdded, onDataRemoved;
	private Consumer<Serializable> onMessageReceived;

	/**
	 * Creates a new virtual distributed platform. The created platform will
	 * only know the local peer and does not actively connect to other peers or
	 * receive connections from such until a <code>connect</code> method is
	 * called.
	 */
	public VDP() {
		// TODO Auto-generated constructor stub

	}

	/**
	 * Connects to a virtual IP address and returns immediately. If other peers
	 * are found at that address or later connect to it, they are added to the
	 * lists of peers, returned by {@link #getAllPeers()},
	 * {@link #getRemotePeers()} and the <code>onPeerConnected</code>-listener
	 * is informed.
	 *
	 * @param multicastAddress
	 *            a multicast address (in the range 224.0.0.0 to 239.255.255.255
	 *            for IPv4)
	 * @throws IOException
	 *             if the address cannot be connected to
	 * @see #disconnectFromMulticastAddress(String)
	 * @see #setOnPeerConnected(Consumer)
	 * @see #setOnPeerDisconnected(Consumer)
	 */
	public void connectToMulticastAddress(String multicastAddress) throws IOException {

	}

	public void connectToExternal(String address) throws IOException {

	}

	public void disconnectFromMulticastAddress(String multicastAddress) throws IOException {

	}

	public void disconnectFromExternal(String address) throws IOException {

	}

	public void disconnectAll(BiConsumer<String, IOException> errorHandler) {

	}

	/**
	 * Allows other peers to read the given file. The file will be available
	 * using {@link Peer#getRootFiles()} or {@link Peer#getFile(String)} where
	 * the path equals the returned filename. If the file is a directory, all
	 * contained files and folders are also shared.
	 * <p>
	 * <i>Warning:</i> While mounted, files should not be modified.
	 * </p>
	 *
	 * @return the mounted filename with which other peers can access the file.
	 *         This may be different from the real file name if a file with that
	 *         name has already been mounted before.
	 * @param file
	 *            file to share
	 * @see #mountFile(String, File)
	 * @see #unmountFile(String)
	 */
	public String mountFile(File file) {
		return null;
	}

	/**
	 * Mounts a file with a given name. The same file can be mounted with
	 * different names. See {@link #mountFile(File)} for further details.
	 *
	 * @param name
	 *            the name by which the file can be found
	 * @param file
	 *            the file to mount
	 * @throws IllegalArgumentException
	 *             if the given name is already in use
	 * @see #mountFile(File)
	 * @see #unmountFile(String)
	 */
	public void mountFile(String name, File file) throws IllegalArgumentException {

	}

	public void mountVirtual(String name, long length, long lastModified, Supplier<InputStream> streamSupplier)
			throws IllegalArgumentException {

	}

	public void unmountFile(String name) {

	}

	List<RemoteFile> getLocalRootFiles() {
		return null;
	}

	RemoteFile getLocalFile(String path) {
		return null;
	}

	public void putData(Distributed data) {
		if (data.vdp != null)
			throw new IllegalArgumentException("data is already bound");
		data.vdp = this;

	}

	public void removeData(Distributed data) {
		if (data.vdp != this)
			throw new IllegalArgumentException();
		data.vdp = null;

	}

	public Optional<Distributed> getData(String id) {
		return null;
	}

	public List<Distributed> getAllData() {
		return null;
	}

	public void saveAllData(File saveFile) {

	}

	public void loadAllData(File saveFile) {

	}

	public Peer getLocalPeer() {
		return localPeer;
	}

	public List<Peer> getRemotePeers() {
		return null;
	}

	public List<Peer> getAllPeers() {
		return null;
	}

	public Peer getPeer(String id) {
		return null;
	}

	void changed(Distributed distributed) {
		// TODO Auto-generated method stub

	}

	public Consumer<ConnectionEvent> getOnPeerConnected() {
		return onPeerConnected;
	}

	public void setOnPeerConnected(Consumer<ConnectionEvent> onPeerConnected) {
		this.onPeerConnected = onPeerConnected;
	}

	public Consumer<ConnectionEvent> getOnPeerDisconnected() {
		return onPeerDisconnected;
	}

	public void setOnPeerDisconnected(Consumer<ConnectionEvent> onPeerDisconnected) {
		this.onPeerDisconnected = onPeerDisconnected;
	}

	public Consumer<Distributed> getOnDataAdded() {
		return onDataAdded;
	}

	public void setOnDataAdded(Consumer<Distributed> onDataAdded) {
		this.onDataAdded = onDataAdded;
	}

	public Consumer<Distributed> getOnDataRemoved() {
		return onDataRemoved;
	}

	public void setOnDataRemoved(Consumer<Distributed> onDataRemoved) {
		this.onDataRemoved = onDataRemoved;
	}

	public Consumer<Serializable> getOnMessageReceived() {
		return onMessageReceived;
	}

	public void setOnMessageReceived(Consumer<Serializable> onMessageReceived) {
		this.onMessageReceived = onMessageReceived;
	}

}
