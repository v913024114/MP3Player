package com.mp3player.fx.icons;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

public class FXIcons {


	public static ImageView get(String filename, double height) {
		ImageView settingsImage;
		try {
			settingsImage = new ImageView(new Image(FXIcons.class.getResource(filename).toExternalForm()));
		} catch(Exception exc) {
			settingsImage = new ImageView();
		}
		settingsImage.setFitHeight(height * dpiFactor());
		settingsImage.setPreserveRatio(true);
		return settingsImage;
	}

    private static double dpiFactor() {
	    return Font.getDefault().getSize() / 12.0;
	}
}
