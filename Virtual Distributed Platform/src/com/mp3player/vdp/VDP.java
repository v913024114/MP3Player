package com.mp3player.vdp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Main class for setting up a virtual distributed platform.
 * @author Philipp Holl
 *
 */
public class VDP {
	private Peer localPeer;

	private Consumer<ConnectionEvent> onPeerConnected, onPeerDisconnected;
	private Consumer<Distributed> onDataAdded, onDataRemoved;
	private Consumer<Serializable> onMessageReceived;


	public VDP() {
		// TODO Auto-generated constructor stub
	}


	public void connectToMulticastAddress(String multicastAddress) throws IOException {

	}

	public void connectToExternal(String address) throws IOException {

	}


	public void mountFile(File file) {

	}

	public void mountFile(String name, File file) {

	}

	public void mountVirtual(String name, long length, long lastModified, Supplier<InputStream> streamSupplier) {

	}

	public void unmountFile(File file) {

	}

	public void unmountFile(String name) {

	}

	List<RemoteFile> getLocalRootFiles() {

	}

	RemoteFile getLocalFile(String path) {

	}


	public void putData(Distributed data) {
		if(data.vdp != null) throw new IllegalArgumentException("data is already bound");
		data.vdp = this;


	}

	public void removeData(Distributed data) {
		if(data.vdp != this) throw new IllegalArgumentException();
		data.vdp = null;


	}

	public Optional<Distributed> getData(String id) {

	}

	public List<Distributed> getAllData() {

	}

	public void saveAllData(File saveFile) {

	}

	public void loadAllData(File saveFile) {

	}

	public void send(Peer peer, Serializable message) {

	}

	public Peer getLocalPeer() {
		return localPeer;
	}

	public List<Peer> getRemotePeers() {

	}

	public List<Peer> getAllPeers() {

	}

	public Peer getPeer(String id) {

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
