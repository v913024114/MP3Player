package com.mp3player.model;

import java.io.Serializable;
import java.util.function.Predicate;

import com.mp3player.player.data.Media;

public interface MediaFilter extends Predicate<Media>, Serializable {

}
