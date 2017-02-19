package mp3player.player.data;

import java.io.Serializable;

public class Media implements Serializable {
	private static final long serialVersionUID = 7773145268758867752L;


	private String peerID;
	private String path;


	public Media(String peerID, String path) {
		this.peerID = peerID;
		this.path = path;
	}


	public String getPeerID() {
		return peerID;
	}


	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return path;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((peerID == null) ? 0 : peerID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Media other = (Media) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (peerID == null) {
			if (other.peerID != null)
				return false;
		} else if (!peerID.equals(other.peerID))
			return false;
		return true;
	}




}
