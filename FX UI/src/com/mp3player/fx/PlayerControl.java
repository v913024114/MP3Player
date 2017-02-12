package com.mp3player.fx;

import com.sun.javafx.util.Utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class PlayerControl extends Control {
	private DoubleProperty duration = new DoublePropertyBase(0) {
        @Override protected void invalidated() {
            adjustPosition();
            notifyAccessibleAttributeChanged(AccessibleAttribute.VALUE);
        }

        @Override
        public Object getBean() {
            return PlayerControl.this;
        }

        @Override
        public String getName() {
            return "duration";
        }
    };
	public double getDuration() { return durationProperty().get(); }
	public void setDuration(double duration) { durationProperty().set(duration); }
	public DoubleProperty durationProperty() { return duration; }



	private DoubleProperty position = new DoublePropertyBase(0) {
        @Override protected void invalidated() {
            adjustPosition();
            notifyAccessibleAttributeChanged(AccessibleAttribute.MAX_VALUE);
        }

        @Override
        public Object getBean() {
            return PlayerControl.this;
        }

        @Override
        public String getName() {
            return "position";
        }
    };
	public final DoubleProperty positionProperty() { return position; }
	public double getPosition() { return positionProperty().get(); }
	public void setPosition(double position) { positionProperty().set(position); }



	public PlayerControl() {
		getStyleClass().add("time-slider");
	}


	@Override
	protected Skin<?> createDefaultSkin() {
		return new RoundPlayerSkin(this);
	}

	/**
     * Ensures that pos < duration
     */
    private void adjustPosition() {
        if (getPosition() < 0 || getPosition() > getDuration())
             setPosition(Utils.clamp(0, getPosition(), getDuration()));
    }
}