package mp3player.player.data;

import java.util.ArrayList;
import java.util.List;

import com.mp3player.vdp.Conflict;
import com.mp3player.vdp.Distributed;

public class PlaybackStatus extends Distributed {
	private static final long serialVersionUID = -7737670409460902583L;

	public static final String VDP_ID = "playback-status";

	private String device;
	private List<String> supportedFormats;

	private String currentMedia; // media ID

	private double gain, minGain, maxGain;
	private boolean mute;

	private boolean playing;
	private boolean busy;
	private String busyText; // or error text when not playing & not busy

	private double lastKnownPosition; // in seconds
	private long lastUpdateTime; // in milliseconds
	private double duration;


	public PlaybackStatus() {
		super(VDP_ID);
		supportedFormats = new ArrayList<>(0);
	}


	@Override
	public Distributed resolveConflict(Conflict conflict) {
		// TODO Auto-generated method stub
		return null;
	}


	public void setStatus(String currentMedia, double gain, double minGain, double maxGain, boolean mute, boolean playing,
			boolean busy, String busyText, double lastKnownPosition, long lastUpdateTime, double duration) {
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
		if(!playing) return lastKnownPosition;
		return lastKnownPosition + (System.currentTimeMillis()-lastUpdateTime) / 1e3;
	}


	public String getDevice() {
		return device;
	}


	public void setDevice(String device) {
		this.device = device;
		fireChangedLocally();
	}


	public String getCurrentMedia() {
		return currentMedia;
	}


	public void setCurrentMedia(String currentMedia) {
		this.currentMedia = currentMedia;
		fireChangedLocally();
	}


	public double getGain() {
		return gain;
	}


	public void setGain(double gain) {
		this.gain = gain;
		fireChangedLocally();
	}


	public double getMinGain() {
		return minGain;
	}


	public void setMinGain(double minGain) {
		this.minGain = minGain;
		fireChangedLocally();
	}


	public double getMaxGain() {
		return maxGain;
	}


	public void setMaxGain(double maxGain) {
		this.maxGain = maxGain;
		fireChangedLocally();
	}


	public boolean isMute() {
		return mute;
	}


	public void setMute(boolean mute) {
		this.mute = mute;
		fireChangedLocally();
	}


	public boolean isPlaying() {
		return playing;
	}


	public void setPlaying(boolean playing) {
		this.playing = playing;
		fireChangedLocally();
	}


	public boolean isBusy() {
		return busy;
	}


	public void setBusy(boolean busy) {
		this.busy = busy;
		fireChangedLocally();
	}


	public String getBusyText() {
		return busyText;
	}


	public void setBusyText(String busyText) {
		this.busyText = busyText;
		fireChangedLocally();
	}


	public double getLastKnownPosition() {
		return lastKnownPosition;
	}


	public void setLastKnownPosition(double lastKnownPosition) {
		this.lastKnownPosition = lastKnownPosition;
		fireChangedLocally();
	}


	public long getLastUpdateTime() {
		return lastUpdateTime;
	}


	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
		fireChangedLocally();
	}


	public double getDuration() {
		return duration;
	}


	public void setDuration(double duration) {
		this.duration = duration;
		fireChangedLocally();
	}



}
