package com.mp3player.audio2.javasound.lib;

import javax.sound.sampled.AudioSystem;

import com.mp3player.desktopaudio.MediaFile;
import com.mp3player.desktopaudio.MediaFormat;
import com.mp3player.desktopaudio.MediaInfo;
import com.mp3player.desktopaudio.MediaStream;

public class DefaultMediaInfo extends MediaInfo {
	private long contentSize;
	
	

	public DefaultMediaInfo(MediaFile media, MediaFormat format) {
		super(format);
		contentSize = media.getFileSize() - 44; // 44 bytes for wav
	}
	
	public DefaultMediaInfo(MediaStream stream, MediaFormat format) {
		super(format);
		contentSize = stream.getStreamLength();
	}

	@Override
	public String getTitle() {
		return (String) format.getProperty("title");
	}

	@Override
	public double getDuration() {
		int frameLength = format.getFrameLength();
		float framerate = format.getAudioDataFormat().getFrameRate();
		if(frameLength == AudioSystem.NOT_SPECIFIED || framerate == AudioSystem.NOT_SPECIFIED)
			return -1;
		return frameLength / framerate;
	}

	@Override
	public double estimateDuration() {
		// Exact value
		double exact = getDuration();
		if(exact != -1) return exact;
		
		// Estimate from estimateFrameLength()
		long frameLength = estimateFrameLength();
		float framerate = format.getAudioDataFormat().getFrameRate();
		if(framerate == AudioSystem.NOT_SPECIFIED) return -1;
		return frameLength / framerate;
	}

	public long estimateFrameLength() {
		if(format.getFrameLength() >= 0) return format.getFrameLength();
		
		// Estimate from file size
		long filesize = contentSize;
		if(filesize < 0) return -1;
		int frameSize = format.getAudioDataFormat().getFrameSize();
		if(frameSize <= 0) return -1;
		long frameLength = (filesize) / frameSize; 
		return frameLength;
	}

}
