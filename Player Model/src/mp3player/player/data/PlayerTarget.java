package mp3player.player.data;

import com.mp3player.vdp.Conflict;
import com.mp3player.vdp.Distributed;

public class PlayerTarget extends Distributed {
	private static final long serialVersionUID = 3507019847042275473L;

	public static final String VDP_ID = "player-target";

	private String targetDevice; // only set if device is to change
	private String targetMedia; // only set if media is to change
	private double targetGain;
	private boolean targetMute;
	private boolean targetPlaying;
	private double targetPosition = -1; // -1 if not to change


	public PlayerTarget() {
		super(VDP_ID, true, false);
	}

	@Override
	public Distributed resolveConflict(Conflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTargetDevice() {
		return targetDevice;
	}

	public void setTargetDevice(String targetDevice) {
		this.targetDevice = targetDevice;
		fireChangedLocally();
	}

	public String getTargetMedia() {
		return targetMedia;
	}

	public void setTargetMedia(String targetMedia, boolean startPlayingImmediately) {
		this.targetMedia = targetMedia;
		if(startPlayingImmediately) {
			targetPlaying = true;
		}
		fireChangedLocally();
	}

	public double getTargetGain() {
		return targetGain;
	}

	public void setTargetGain(double targetGain) {
		this.targetGain = targetGain;
		fireChangedLocally();
	}

	public boolean isTargetMute() {
		return targetMute;
	}

	public void setTargetMute(boolean targetMute) {
		this.targetMute = targetMute;
		fireChangedLocally();
	}

	public boolean isTargetPlaying() {
		return targetPlaying;
	}

	public void setTargetPlaying(boolean targetPlaying) {
		this.targetPlaying = targetPlaying;
		fireChangedLocally();
	}

	public double getTargetPosition() {
		return targetPosition;
	}

	public boolean isTargetPositionSet() {
		return targetPosition >= 0;
	}

	public void setTargetPosition(double targetPosition) {
		this.targetPosition = targetPosition;
		fireChangedLocally();
	}


}
