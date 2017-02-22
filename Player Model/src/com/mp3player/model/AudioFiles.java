package com.mp3player.model;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AudioFiles {
	public static final List<String> AUDIO_FILE_EXTENSIONS = Arrays.asList(
			"mp3", "ogg", "wav", "aif", "aiff"
			);


	private  AudioFiles() {}


	public static List<File> allAudioFilesIn(File file) {
		return Arrays.asList(file.listFiles(f-> isAudioFile(f)));
	}

	public static boolean isAudioFile(File file) {
		return isAudioFile(file.getName());
	}

	public static boolean isAudioFile(String filename) {
		return AUDIO_FILE_EXTENSIONS.stream().anyMatch(ext -> filename.toLowerCase().endsWith("."+ext));
	}

	public static List<File> trim(List<File> arbitraryFileList) {
		return arbitraryFileList.stream().filter(file -> isAudioFile(file)).collect(Collectors.toList());
	}
}
