package com.mp3player.model;

import java.io.Serializable;
import java.util.function.Predicate;

public interface MediaFilter extends Predicate<MediaInfo>, Serializable {

}
