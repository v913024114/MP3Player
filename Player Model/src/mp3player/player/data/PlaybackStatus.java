package mp3player.player.data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mp3player.vdp.Conflict;
import com.mp3player.vdp.Distributed;

/**
 * This shared data object reflects the current status of playback. It is only
 * manipulated by the playback engine and contains properties that may be
 * displayed by the application.
 *
 * @author Philipp Holl
 *
 */
public class PlaybackStatus extends Distributed {
	private static final long serialVersionUID = -5405377670442949541L;

	public static final String VDP_ID = "playback-status";

	private String device;
	private List<String> supportedFormats = Collections.emptyList();

	private Optional<String> currentMedia = Optional.empty(); // media ID

	private double gain, minGain, maxGain;
	private boolean mute;

	private boolean playing;
	private boolean busy;
	private String busyText; // or error text when not playing & not busy

	private double lastKnownPosition; // in seconds
	private long lastUpdateTime; // in milliseconds
	private double duration;

	public PlaybackStatus() {
		super(VDP_ID, false, false);
	}

	@Override
	public Distributed resolveConflict(Conflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setStatus(String device, List<String> supportedFormats, Optional<String> currentMedia, double gain,
			double minGain, double maxGain, boolean mute, boolean playing, boolean busy, String busyText,
			double lastKnownPosition, long lastUpdateTime, double duration) {
		if(currentMedia == null) throw new IllegalArgumentException("currentMedia = null");
		if(supportedFormats == null) throw new IllegalArgumentException("supportedFormats = null");
		this.device = device;
		this.supportedFormats = supportedFormats;
		this.currentMedia = currentMedia;
		this.gain = gain;
		this.minGain = minGain;
		this.maxGain = maxGain;
		this.mute = mute;
		this.playing = playing;
		this.busy = busy;
		this.busyText = busyText;
		this.lastKnownPosition = lastKnownPosition;
		this.lastUpdateTime = lastUpdateTime;
		this.duration = duration;
		fireChangedLocally();
	}

	public void updatePosition(double lastKnownPosition, long lastUpdateTime) {
		this.lastKnownPosition = lastKnownPosition;
		this.lastUpdateTime = lastUpdateTime;
		fireChangedLocally();
	}

	public double getCurrentPosition() {
		if (!playing)
			return lastKnownPosition;
		return lastKnownPosition + (System.currentTimeMillis() - lastUpdateTime) / 1e3;
	}

	public String getDevice() {
		return device;
	}

	public Optional<String> getCurrentMedia() {
		return currentMedia;
	}

	public List<String> getSupportedFormats() {
		return supportedFormats;
	}

	public double getGain() {
		return gain;
	}

	public double getMinGain() {
		return minGain;
	}

	public double getMaxGain() {
		return maxGain;
	}

	public boolean isMute() {
		return mute;
	}

	public boolean isPlaying() {
		return playing;
	}

	public boolean isBusy() {
		return busy;
	}

	public String getBusyText() {
		return busyText;
	}

	public double getLastKnownPosition() {
		return lastKnownPosition;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public double getDuration() {
		return duration;
	}

}
