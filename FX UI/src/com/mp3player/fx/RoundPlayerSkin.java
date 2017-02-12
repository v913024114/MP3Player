package com.mp3player.fx;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.converter.DoubleStringConverter;

public class RoundPlayerSkin extends SkinBase<PlayerControl>
{
	private CircularSlider slider;

	// Buttons
	private Group buttonRoot;
	private Button playButton, stopButton, prevButton, nextButton;
	private double innerMargin = 2, outerMargin = 5;


	public RoundPlayerSkin(PlayerControl control) {
		super(control);

		slider = new CircularSlider();
		slider.setLabelFormatter(new DoubleStringConverter() {
			@Override
			public String toString(Double time) {
				if(time < 60) {
					return (int) Math.round(time) + " s";
				} else if(time < 60*60){
					int min = (int) (time / 60);
					int sec = (int) Math.round(time - min*60);
					return String.format("%d:%02d", min, sec);
				} else {
					int hrs = (int) (time / 60 / 60);
					int min = (int) ((time - hrs*60) / 60);
					int sec = (int) Math.round((time - hrs*60*60 - min*60));
					return String.format("%d:%02d:%02d", hrs, min, sec);
				}
			}
		});
		getChildren().add(slider);

		createButtons();
	}


	private void createButtons() {
		BorderPane buttonPane = new BorderPane();
		buttonPane.setPickOnBounds(false);
		buttonRoot = new Group(buttonPane);
		getChildren().add(buttonRoot);
		buttonPane.setCenter(playButton = new Button("Play"));

		BorderPane bottomButtonPane = new BorderPane();
		bottomButtonPane.setPickOnBounds(false);
		Group bottomRoot = new Group(bottomButtonPane);
		buttonPane.setBottom(bottomRoot);
		BorderPane.setAlignment(bottomRoot, Pos.TOP_CENTER);

		bottomButtonPane.setCenter(stopButton = new Button("Stop"));
		bottomButtonPane.setLeft(prevButton = new Button("<<"));
		BorderPane.setAlignment(prevButton, Pos.TOP_RIGHT);
		bottomButtonPane.setRight(nextButton = new Button(">>"));

		Insets buttonMargins = new Insets(innerMargin);
		BorderPane.setMargin(playButton, buttonMargins);
		BorderPane.setMargin(stopButton, buttonMargins);
		BorderPane.setMargin(prevButton, buttonMargins);
		BorderPane.setMargin(nextButton, buttonMargins);

		playButton.setPickOnBounds(false);
		stopButton.setPickOnBounds(false);
		prevButton.setPickOnBounds(false);
		nextButton.setPickOnBounds(false);
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

	private static void setButtonShape(Button button, Shape shape) {
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
    }

}