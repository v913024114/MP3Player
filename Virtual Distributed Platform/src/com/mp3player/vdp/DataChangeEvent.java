package com.mp3player.vdp;

public class DataChangeEvent {
	private Peer editingClient;
	private Peer localClient;
	private long sourceTime;
	private long timeReceived;


	public boolean wasChangedLocally() {
		return editingClient.equals(localClient);
	}
}
