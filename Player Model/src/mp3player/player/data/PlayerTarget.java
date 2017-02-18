package mp3player.player.data;

import java.util.Optional;
import java.util.OptionalDouble;

import com.mp3player.vdp.Conflict;
import com.mp3player.vdp.Distributed;

/**
 * This shared data object contains all commands for the playback engine. This
 * includes what media to play, the volume, etc. Except for the target position,
 * all fields should indicate the values the playback engine should use.
 *
 * @author Philipp Holl
 *
 */
public class PlayerTarget extends Distributed {
	private static final long serialVersionUID = 3507019847042275473L;

	public static final String VDP_ID = "player-target";

	private String targetDevice;

	/**
	 * if empty, dispose of player
	 */
	private Optional<String> targetMedia = Optional.empty();

	private double targetGain;
	private boolean targetMute;
	private boolean targetPlaying;

	private OptionalDouble targetPosition = OptionalDouble.empty();
	/** The time at which the target position request was issued */
	private long positionUpdateTime;

	private boolean loop;
	private boolean shuffled;

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

	public Optional<String> getTargetMedia() {
		return targetMedia;
	}

	public void setTargetMedia(Optional<String> targetMedia, boolean startPlayingImmediately) {
		this.targetMedia = targetMedia;
		if (startPlayingImmediately) {
			targetPlaying = true;
		}
		if(!targetMedia.isPresent()) {
			targetPlaying = false;
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

	public OptionalDouble getTargetPosition() {
		return targetPosition;
	}

	public long getPositionUpdateTime() {
		return positionUpdateTime;
	}

	public void setTargetPosition(double targetPosition, boolean startPlaying) {
		this.targetPosition = OptionalDouble.of(targetPosition);
		positionUpdateTime = System.currentTimeMillis();
		if(startPlaying) {
			targetPlaying = true;
		}
		fireChangedLocally();
	}

	public boolean wasTargetPositionSetAfter(long lastUpdateTime) {
		return positionUpdateTime > lastUpdateTime && targetPosition.isPresent();
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
		fireChangedLocally();
	}

	public void stop() {
		targetPlaying = false;
		setTargetPosition(0, false);
	}

	public boolean isShuffled() {
		return shuffled;
	}

	public void setShuffled(boolean shuffled) {
		this.shuffled = shuffled;
		fireChangedLocally();
	}


}
