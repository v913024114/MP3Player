package com.mp3player.mediacommand;

import java.util.EventListener;

public interface CombinationListener extends EventListener {

	void onCombination(MediaCommand[] combination);
	
}
