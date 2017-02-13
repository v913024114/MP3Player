package com.mp3player.vdp;

public class DataChangeEvent {
	private Peer editingClient;
	private Peer localClient;
	private long sourceTime;
	private long timeReceived;


	public DataChangeEvent(Peer editingClient, Peer localClient, long sourceTime, long timeReceived) {
		this.editingClient = editingClient;
		this.localClient = localClient;
		this.sourceTime = sourceTime;
		this.timeReceived = timeReceived;
	}


	public boolean wasChangedLocally() {
		return editingClient.equals(localClient);
	}


	public Peer getEditingClient() {
		return editingClient;
	}


	public Peer getLocalClient() {
		return localClient;
	}


	public long getSourceTime() {
		return sourceTime;
	}


	public long getTimeReceived() {
		return timeReceived;
	}


}
