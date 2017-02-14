package com.mp3player.fx;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class RoundPlayerSkin extends SkinBase<PlayerControl>
{
	// General circular arc
	private Shape foregroundMask; // invisible, but throws shadow
	private DropShadow shadow;
	private Path arcMask;
	private double radius;

	// Buttons
	private Group buttonRoot;
	private Button playButton, stopButton, prevButton, nextButton;
	private Insets buttonMargins = new Insets(2);

	// Ticks
	private Group centralGroup;
	private List<TimeTick> majorTicks = new ArrayList<>(); // 60 seconds
	private List<TimeTick> minorTicks = new ArrayList<>(); // 10 seconds



	public RoundPlayerSkin(PlayerControl control) {
		super(control);

		shadow = new DropShadow();
		shadow.setColor(new Color(0, 0, 0, 0.6));

		centralGroup = new Group();
		getChildren().add(centralGroup);

		Interpolator i = Interpolator.EASE_BOTH;
		new Timeline(new KeyFrame(new Duration(0), new KeyValue(shadow.offsetXProperty(), 20, i), new KeyValue(shadow.offsetYProperty(), 20, i)),
				new KeyFrame(new Duration(1000), new KeyValue(shadow.offsetXProperty(), 20, i), new KeyValue(shadow.offsetYProperty(), 20, i)),
				new KeyFrame(new Duration(1900), new KeyValue(shadow.offsetXProperty(), 5, i), new KeyValue(shadow.offsetYProperty(), 10, i)),
				new KeyFrame(new Duration(3400), new KeyValue(shadow.offsetXProperty(), 0, i), new KeyValue(shadow.offsetYProperty(), 0, i)))
		.playFromStart();

//		List<CssMetaData<? extends Styleable, ?>> css = getCssMetaData(); // TODO
//		css.forEach(c -> System.out.println(c));
//		System.out.println();
//		control.getCssMetaData().forEach(c -> System.out.println(c));

		createButtons();
		createShapes(200,200, 0, 0); // default size

		control.durationProperty().addListener((e,o,n) -> update());
	}


	private void createButtons() {
		BorderPane buttonPane = new BorderPane();
		buttonRoot = new Group(buttonPane);
		getChildren().add(buttonRoot);
		buttonPane.setCenter(playButton = new Button("Play"));

		BorderPane bottomButtonPane = new BorderPane();
		Group bottomRoot = new Group(bottomButtonPane);
		buttonPane.setBottom(bottomRoot);
		BorderPane.setAlignment(bottomRoot, Pos.TOP_CENTER);

		bottomButtonPane.setCenter(stopButton = new Button("Stop"));
		bottomButtonPane.setLeft(prevButton = new Button("<<"));
		BorderPane.setAlignment(prevButton, Pos.TOP_RIGHT);
		bottomButtonPane.setRight(nextButton = new Button(">>"));

		BorderPane.setMargin(playButton, buttonMargins);
		BorderPane.setMargin(stopButton, buttonMargins);
		BorderPane.setMargin(prevButton, buttonMargins);
		BorderPane.setMargin(nextButton, buttonMargins);
	}


	private void update() {
		double duration = getSkinnable().getDuration();
		int minutes = (int) (duration / 60);

		for(int i = 0; i <= minutes; i++) {
			TimeTick tick = new TimeTick(i*60, 60, Math.PI / 64, Color.LIGHTGRAY);
			tick.fitBounds(radius, arcMask.getStrokeWidth());
			majorTicks.add(tick);
			tick.updatePosition(duration);

			centralGroup.getChildren().add(tick.getNode());
		}
		
		int tenSeconds = (int) (duration / 10);
		for(int i = 0; i <= tenSeconds; i++) {
			TimeTick tick = new TimeTick(i*10, 10, Math.PI / 256, Color.LIGHTGRAY);
			tick.fitBounds(radius, arcMask.getStrokeWidth());
			majorTicks.add(tick);
			tick.updatePosition(duration);

			centralGroup.getChildren().add(tick.getNode());
		}


		foregroundMask.toFront();
	}


	/**
	 * Called when the size of the control changes.
	 * @param contentWidth
	 * @param contentHeight
	 * @param radius
	 * @param arcWidth
	 */
	private void createShapes(double contentWidth, double contentHeight, double radius, double arcWidth) {
		this.radius = radius;

		if(foregroundMask != null) {
			getChildren().remove(foregroundMask);
			getChildren().remove(playButton);
		}

		arcMask = arcMask(radius, arcWidth);

		Rectangle fill = new Rectangle(contentWidth*2, contentHeight*2);
		fill.setX(-contentWidth);
		fill.setY(-contentHeight);

		foregroundMask = Shape.subtract(fill, arcMask);
		foregroundMask.setFill(Color.LIGHTGRAY); // TODO use CSS styling / default color
		foregroundMask.setEffect(shadow);
		foregroundMask.setClip(arcMask); // do not draw shadow outside
		getChildren().add(foregroundMask);

		layoutButtons((radius-arcWidth/2) * 0.95);

		update();
	}

	private static Path arcMask(double radius, double arcWidth) {
		Path arcMask = new Path();
		arcMask.setStrokeLineCap(StrokeLineCap.BUTT);
		arcMask.setStrokeWidth(arcWidth);
		arcMask.getElements().add(new MoveTo(0, -radius));
		arcMask.getElements().add(new ArcTo(radius, radius, 0, 0, radius, true, true));
		arcMask.getElements().add(new ArcTo(radius, radius, 0, 0, -radius, true, true));
		return arcMask;
	}

	private void layoutButtons(double rad) {
		double splitV = 0.3; // -1 to 1
		double splitH = 0.3;
		double inset = buttonMargins.getTop();
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
    	double rad = Math.min(contentWidth, contentHeight)*0.4;
    	createShapes(contentWidth, contentHeight, rad, rad*0.2);
    	layoutInArea(foregroundMask, contentX, contentY, contentWidth, contentHeight, 0, HPos.CENTER, VPos.CENTER);
    	layoutInArea(buttonRoot, contentX, contentY, contentWidth, contentHeight, 0, HPos.CENTER, VPos.CENTER);
    	centralGroup.setTranslateX(contentWidth/2);
    	centralGroup.setTranslateY(contentHeight/2);
    }

}