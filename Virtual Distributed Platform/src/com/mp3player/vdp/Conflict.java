package com.mp3player.vdp;

public class Conflict {
	private Distributed local, remote;
	private DataChangeEvent lastLocalChange, lastRemoteChange;

	public Conflict(Distributed local, Distributed remote, DataChangeEvent lastLocalChange,
			DataChangeEvent lastRemoteChange) {
		this.local = local;
		this.remote = remote;
		this.lastLocalChange = lastLocalChange;
		this.lastRemoteChange = lastRemoteChange;
	}

	public Distributed getLocal() {
		return local;
	}

	public Distributed getRemote() {
		return remote;
	}

	/**
	 * Returns the last change of the local data object. If the object was not
	 * manipulated after being added to the {@link VDP}, an event describing the
	 * initial check-in will be returned.
	 *
	 * @return the last change of the local data object
	 */
	public DataChangeEvent getLastLocalChange() {
		return lastLocalChange;
	}

	/**
	 * Returns the last known change of the remote data object. If the conflict
	 * arises from data being added locally and the remote data was not changed
	 * since it was received, an event describing the first recepit of the
	 * object is returned.
	 *
	 * @return the last change of the remote data object
	 */
	public DataChangeEvent getLastRemoteChange() {
		return lastRemoteChange;
	}

}
