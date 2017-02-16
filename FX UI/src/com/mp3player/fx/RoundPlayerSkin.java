package com.mp3player.fx;
import java.io.InputStream;

import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.util.converter.DoubleStringConverter;

// TODO implement indeterminate (ProgressBar)
public class RoundPlayerSkin extends SkinBase<PlayerControl>
{
	private CircularSlider slider;

	// Buttons
	private Group buttonRoot;
	private ToggleButton playButton, repeatButton, shuffleButton;
	private Button stopButton, prevButton, nextButton;
	private double innerMargin = 2, outerMargin = 5;


	public RoundPlayerSkin(PlayerControl control) {
		super(control);
		createSlider();
		createButtons();
	}


	private void createSlider() {
		slider = new CircularSlider();
		slider.setLabelFormatter(new DoubleStringConverter() {
			@Override
			public String toString(Double timeExact) {
				int time = (int) Math.round(timeExact);
				if(time < 60) {
					return time + " s";
				} else if(time < 60*60){
					int min = time / 60;
					int sec = time - min*60;
					return String.format("%d:%02d", min, sec);
				} else {
					int hrs = time / 60 / 60;
					int min = (time - hrs*60) / 60;
					int sec = time - hrs*60*60 - min*60;
					return String.format("%d:%02d:%02d", hrs, min, sec);
				}
			}
		});
		getChildren().add(slider);

		slider.maxProperty().bind(getSkinnable().durationProperty());
		slider.valueProperty().bindBidirectional(getSkinnable().positionProperty());
		slider.disableProperty().bind(getSkinnable().mediaSelectedProperty().not());
	}


	private void createButtons() {
		BorderPane buttonPane = new BorderPane();
		buttonPane.setPickOnBounds(false);
		buttonRoot = new Group(buttonPane);
		getChildren().add(buttonRoot);
		buttonPane.setCenter(playButton = new ToggleButton(null, loadIcon("icons/Play_MouseOff.png", 32, 32)));
		playButton.selectedProperty().bindBidirectional(getSkinnable().playingProperty());

		BorderPane bottomButtonPane = new BorderPane();
		bottomButtonPane.setPickOnBounds(false);
		Group bottomRoot = new Group(bottomButtonPane);
		buttonPane.setBottom(bottomRoot);
		BorderPane.setAlignment(bottomRoot, Pos.TOP_CENTER);

		bottomButtonPane.setCenter(stopButton = new Button(null, loadIcon("icons/Stop_MouseOff.png", 18, 18)));
		bottomButtonPane.setLeft(prevButton = new Button(null, loadIcon("icons/Previous_MouseOff.png", 24, 24)));
		BorderPane.setAlignment(prevButton, Pos.TOP_RIGHT);
		bottomButtonPane.setRight(nextButton = new Button(null, loadIcon("icons/Next_MouseOff.png", 24, 24)));

		getChildren().add(repeatButton = new ToggleButton(null, loadIcon("icons/Repeat_MouseOff.png", 24, 24)));
		getChildren().add(shuffleButton = new ToggleButton(null, loadIcon("icons/Shuffle_MouseOff.png", 24, 24)));
		repeatButton.selectedProperty().bindBidirectional(getSkinnable().repeatProperty());
		shuffleButton.selectedProperty().bindBidirectional(getSkinnable().shuffledProperty());

		Insets buttonMargins = new Insets(innerMargin);
		BorderPane.setMargin(playButton, buttonMargins);
		BorderPane.setMargin(stopButton, buttonMargins);
		BorderPane.setMargin(prevButton, buttonMargins);
		BorderPane.setMargin(nextButton, buttonMargins);

		playButton.setPickOnBounds(false);
		stopButton.setPickOnBounds(false);
		prevButton.setPickOnBounds(false);
		nextButton.setPickOnBounds(false);

		playButton.setTooltip(new Tooltip("Play / Pause"));
		stopButton.setTooltip(new Tooltip("Stop"));
		prevButton.setTooltip(new Tooltip("Previous song"));
		nextButton.setTooltip(new Tooltip("Next song"));
		shuffleButton.setTooltip(new Tooltip("Shuffled playlist"));
		repeatButton.setTooltip(new Tooltip("Repeat playlist"));

		playButton.disableProperty().bind(getSkinnable().mediaSelectedProperty().not());
		stopButton.disableProperty().bind(getSkinnable().mediaSelectedProperty().not());
		nextButton.disableProperty().bind(getSkinnable().mediaSelectedProperty().not().or(getSkinnable().playlistAvailableProperty().not()));
		prevButton.disableProperty().bind(getSkinnable().mediaSelectedProperty().not().or(getSkinnable().playlistAvailableProperty().not()));
	}

	private void layoutButtons(double rad) {
		double splitV = 0.3; // -1 to 1
		double splitH = 0.3;
		double inset = innerMargin;
		Circle region = new Circle(rad);
		setButtonShape(playButton, Shape.intersect(region, new Rectangle(-rad, -rad, 2*rad, (1+splitV)*rad-inset)));
		setButtonShape(stopButton, Shape.intersect(region, new Rectangle(-rad*splitH+inset, splitV*rad+inset, 2*splitH*rad-2*inset, (1-splitV)*rad-inset)));
		setButtonShape(prevButton, Shape.intersect(region, new Rectangle(-rad, splitV*rad+inset, (1-splitH)*rad-inset, (1-splitV)*rad-inset)));
		setButtonShape(nextButton, Shape.intersect(region, new Rectangle(rad*splitH+inset, splitV*rad+inset, (1-splitH)*rad-inset, (1-splitV)*rad-inset)));
	}

	private static void setButtonShape(ButtonBase button, Shape shape) {
		Bounds b = shape.getBoundsInLocal();
		button.setShape(shape);
		button.setMinSize(b.getWidth(), b.getHeight());
		button.setMaxSize(b.getWidth(), b.getHeight());
	}

		@Override
	    protected double computeMinWidth(double height,
	            double topInset, double rightInset,
	            double bottomInset, double leftInset) {
	        return height;
	    }
//
		@Override
		protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset,
				double leftInset) {
			return width;
		}

    @Override
    protected void layoutChildren(double contentX, double contentY,
            double contentWidth, double contentHeight) {
    	double barRadius = Math.min(contentWidth, contentHeight) * slider.getDiameter() / 2;
    	double barWidth = Math.min(contentWidth, contentHeight) * slider.getThickness();
		layoutButtons((barRadius-barWidth/2) - outerMargin);
    	layoutInArea(slider, contentX, contentY, contentWidth, contentHeight, 0, HPos.CENTER, VPos.CENTER);
    	layoutInArea(buttonRoot, contentX, contentY, contentWidth, contentHeight, 0, HPos.CENTER, VPos.CENTER);

    	double inset = 10;
    	layoutInArea(repeatButton, contentX+inset, contentY+inset, contentWidth-2*inset, contentHeight-2*inset, 0, HPos.LEFT, VPos.BOTTOM);
    	layoutInArea(shuffleButton, contentX+inset, contentY+inset, contentWidth-2*inset, contentHeight-2*inset, 0, HPos.RIGHT, VPos.BOTTOM);
    }


    private static ImageView loadIcon(String filename, double width, double height) {
		return loadImage(RoundPlayerSkin.class.getResourceAsStream(filename), width, height);
	}

    private static ImageView loadImage(InputStream in, double width, double height) {
		if(in == null) return null;
		try {
			ImageView iv = new ImageView(new Image(in, width*dpiFactor(), height*dpiFactor(), true, true));
			return iv;
		}catch(Exception exc) {
			return null;
		}
	}

    private static double dpiFactor() {
	    return Font.getDefault().getSize() / 12.0;
	}

}