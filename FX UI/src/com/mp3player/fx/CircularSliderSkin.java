package com.mp3player.fx;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Group;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

public class CircularSliderSkin extends SkinBase<CircularSlider> {
	private Shape foregroundMask; // invisible, but throws shadow
	private DropShadow shadow;

	private double barRadius, barWidth; // effective values, depend on layout size

	private Group centralGroup;
	private Scale centralScale;

	// Ticks
	private List<TimeTick> ticks;
	private Group tickGroup;
	private Timeline buildAnimation = new Timeline();

	// Bar
	private Region styledBarBox;
	private Path bar;
    private StackPane thumb;

	// Value tooltips
	private Tooltip mouseTooltip, barTooltip;
	private Timeline barTooltipAnimation, mouseTooltipAnimation;
	private long barTooltipFadeInLength = 300, barTooltipFadeOutLength = 500, mouseTooltipFadeInLength = 50, mouseTooltipFadeOutLength = 200;



	public CircularSliderSkin(CircularSlider control) {
		super(control);


		shadow = new DropShadow();
		shadow.setColor(new Color(0, 0, 0, 0.6));

		centralGroup = new Group();
		centralGroup.getTransforms().add(centralScale = new Scale(1, 1));
		getChildren().add(centralGroup);


		bar = new Path();
		bar.setMouseTransparent(true);
		bar.setStrokeLineCap(StrokeLineCap.BUTT);
		centralGroup.getChildren().add(bar);

		tickGroup = new Group();
		tickGroup.setMouseTransparent(true);
		centralGroup.getChildren().add(tickGroup);

		styledBarBox = new Region();
		styledBarBox.getStyleClass().add("bar");
		styledBarBox.setVisible(false);
		styledBarBox.setManaged(false);
		getChildren().add(styledBarBox);

		// Thumb
		thumb = new StackPane() {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                switch (attribute) {
                    case VALUE: return getSkinnable().getValue();
                    default: return super.queryAccessibleAttribute(attribute, parameters);
                }
            }
        };
        thumb.setMouseTransparent(true);
        thumb.setOpacity(0);
        thumb.getStyleClass().setAll("thumb");
        thumb.setAccessibleRole(AccessibleRole.THUMB);
//        thumb.resize(30, 30);
        thumb.setLayoutX(100);
        thumb.setLayoutY(100);
        getChildren().add(thumb);


		// Value tooltips
		mouseTooltip = new Tooltip();
		mouseTooltip.setAutoHide(true);
		mouseTooltip.setOpacity(0);

		barTooltip = new Tooltip();
		barTooltip.setAutoHide(true);
		barTooltip.setOpacity(0);
		barTooltip.textProperty().bindBidirectional(control.valueProperty(), new StringConverter<Number>() {

			@Override
			public String toString(Number object) {
				return getLabelFormatter().toString((double) object);
			}

			@Override
			public Number fromString(String string) {
				return getLabelFormatter().fromString(string);
			}
		});


		// Register listeners

		control.maxProperty().addListener(e -> {rebuildTicks(); rebuildBar();});
		control.minProperty().addListener(e -> {rebuildTicks(); rebuildBar();});
		control.valueProperty().addListener(e -> rebuildBar());

		control.tickLengthProperty().addListener(e -> rebuildTicks());
		control.minorTickLengthProperty().addListener(e -> rebuildTicks());
		control.majorTickUnitProperty().addListener(e -> rebuildTicks());
		control.minorTickCountProperty().addListener(e -> rebuildTicks());

		control.minorTickVisibleProperty().addListener(e -> updateTickVisibility());
		control.tickMarkVisibleProperty().addListener(e -> updateTickVisibility());

		styledBarBox.backgroundProperty().addListener(e -> updateBarStyle());

		getSkinnable().setOnMouseMoved(e -> updateMouseOver(e));
		getSkinnable().setOnMouseExited(e -> {
			mouseTooltipAnimation = fadeTooltip(mouseTooltipFadeOutLength, 0, mouseTooltip, mouseTooltipAnimation);
			thumb.setOpacity(0);
			barTooltipAnimation = fadeTooltip(barTooltipFadeOutLength, 0, barTooltip, barTooltipAnimation);
		});
		getSkinnable().setOnMouseEntered(e -> {
			updateMouseOver(e);
			barTooltipAnimation = fadeTooltip(barTooltipFadeInLength, 1, barTooltip, barTooltipAnimation);
		});
		getSkinnable().setOnMouseReleased(e -> {
			if(isOnBar(e.getX(), e.getY())) {
				getSkinnable().setValue(getValueAt(e.getX(), e.getY()));
			}
		});
	}


	private Timeline fadeTooltip(long duration, double targetOpacity, Tooltip barTooltip, Timeline barTooltipAnimation) {
		if(barTooltipAnimation != null && barTooltipAnimation.getStatus() == Animation.Status.RUNNING) {
			barTooltipAnimation.stop();
		}
		if(targetOpacity > 0 && !barTooltip.isShowing()) {
			showTooltipAt(barTooltip, getSkinnable().getValue());
		}
		barTooltipAnimation = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(barTooltip.opacityProperty(), barTooltip.getOpacity())),
				new KeyFrame(new Duration(duration), new KeyValue(barTooltip.opacityProperty(), targetOpacity)));
		if(targetOpacity == 0) {
			barTooltipAnimation.setOnFinished(e -> {
				barTooltip.hide();
			});
		}
		barTooltipAnimation.play();
		return barTooltipAnimation;
	}


	private void updateMouseOver(MouseEvent e) {
		double pos = getValueAt(e.getX(), e.getY());
		boolean onBar = isOnBar(e.getX(), e.getY());

		// update tooltip
		if(onBar) {
			mouseTooltip.setText(getLabelAt(pos));
			showTooltipAt(mouseTooltip, pos);
			mouseTooltipAnimation = fadeTooltip(mouseTooltipFadeInLength, 1, mouseTooltip, mouseTooltipAnimation);
		} else {
			mouseTooltipAnimation = fadeTooltip(mouseTooltipFadeOutLength, 0, mouseTooltip, mouseTooltipAnimation);
		}

		// update thumb
		Point2D thumbLoc = getLocation(pos, barRadius);
		thumb.setLayoutX(thumbLoc.getX() - thumb.getWidth()/2);
		thumb.setLayoutY(thumbLoc.getY() - thumb.getHeight()/2);
		thumb.setOpacity(onBar ? 1 : 0.4);
	}

	private double getValueAt(double x, double y) {
		double dx = x - getSkinnable().getWidth()/2;
		double dy = y - getSkinnable().getHeight()/2;
		double angle = Math.atan2(dx, -dy);
		if(angle < 0) angle += 2*Math.PI;
		double pos = angle / (2*Math.PI) * (getSkinnable().getMax()-getSkinnable().getMin()) + getSkinnable().getMin();
		return pos;
	}

	private String getLabelAt(double pos) {
		return getLabelFormatter().toString(pos);
	}

	private StringConverter<Double> getLabelFormatter() {
		StringConverter<Double> formatter = getSkinnable().getLabelFormatter();
		if(formatter == null) formatter = new DoubleStringConverter();
		return formatter;
	}

	private Point2D getLocation(double pos, double rad) {
		double angle = 2*Math.PI * (pos-getSkinnable().getMin()) / (getSkinnable().getMax()-getSkinnable().getMin());
		return new Point2D(rad*Math.sin(angle)+getSkinnable().getWidth()/2, -rad*Math.cos(angle)+getSkinnable().getHeight()/2);
	}

	private void showTooltipAt(Tooltip tooltip, double pos) {
		double rad = barRadius + barWidth/2 + 30;
		showTooltipAt(tooltip, getLocation(pos, rad));
	}

	private void showTooltipAt(Tooltip tooltip, Point2D point) {
		CircularSlider control = getSkinnable();
		tooltip.setAnchorX(point.getX() + control.getScene().getX() + control.getScene().getWindow().getX() - tooltip.getWidth()/2);
		tooltip.setAnchorY(point.getY() + control.getScene().getY() + control.getScene().getWindow().getY() - tooltip.getHeight()/2);
		if(!tooltip.isShowing()) {
			tooltip.show(getSkinnable().getScene().getWindow());
		}
	}

	private boolean isOnBar(double x, double y) {
		double dx = x - getSkinnable().getWidth()/2;
		double dy = y - getSkinnable().getHeight()/2;
		double rad = Math.sqrt(dx*dx+dy*dy);
		return rad >= barRadius-barWidth/2 && rad <= barRadius+barWidth/2;
	}


	private void updateBarStyle() {
		Background bg = styledBarBox.getBackground();
		if(bg != null) {
			BackgroundFill fill = bg.getFills().get(bg.getFills().size()-1);
			bar.setStroke(fill.getFill());
		}
	}

	private void updateTickVisibility() {
		boolean mjv = getSkinnable().isShowTickMarks();
		boolean mnv = getSkinnable().isMinorTickVisible();
		for(TimeTick tick : ticks) {
			tick.getNode().setVisible(mjv && (tick.isMajor() ? true : mnv));
		}
	}

	private void updateTicks() {
		if(ticks == null) {
			ticks = new ArrayList<>();
			rebuildTicks();
		}
	}

	private void rebuildTicks() {
		for(TimeTick tick : ticks) {
			tickGroup.getChildren().remove(tick.getNode());
		}
		ticks.clear();

		double min = getSkinnable().getMin();
		double max = getSkinnable().getMax();
		double majorUnit = getSkinnable().getMajorTickUnit();
		double minorUnit = getSkinnable().getMajorTickUnit() / getSkinnable().getMinorTickCount();

		for(double pos = Math.ceil(min / minorUnit) * minorUnit; pos < max; pos += minorUnit) {
			boolean major = isInt(pos/majorUnit, 1e-6);
			TimeTick tick = new TimeTick(pos,
					major,
					(major ? getSkinnable().getTickLength() : getSkinnable().getMinorTickLength()) * 0.006,
					major ? "axis-tick-mark" : "axis-minor-tick-mark");
			tick.fitBounds(barWidth/barRadius);
			ticks.add(tick);
			tick.updatePosition(min, max);
			tickGroup.getChildren().add(tick.getNode());
		}

		updateTickVisibility();


		if(buildAnimation != null && buildAnimation.getStatus() == Timeline.Status.RUNNING) {
			buildAnimation.stop();
		}
		buildAnimation = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(tickGroup.opacityProperty(), 0)),
				new KeyFrame(new Duration(1000), new KeyValue(tickGroup.opacityProperty(), 1)));
		buildAnimation.play();

		foregroundMask.toFront();
	}

	private void rebuildBar() {
		bar.setStrokeWidth(barWidth / barRadius);

		double value = getSkinnable().getValue();
		double fillAngle = 2*Math.PI * (value - getSkinnable().getMin()) / (getSkinnable().getMax()-getSkinnable().getMin());

		bar.getElements().clear();
		bar.getElements().add(new MoveTo(0, -1));
		bar.getElements().add(new ArcTo(1, 1, 0, Math.sin(fillAngle), - Math.cos(fillAngle), fillAngle > Math.PI, true));

		// Thumb
        thumb.resize(barWidth, barWidth);
	}

	private static boolean isInt(double d, double tolerance) {
		return Math.abs(d%1) < tolerance;
	}


	/**
	 * Called when the size of the control changes.
	 * @param contentWidth
	 * @param contentHeight
	 * @param radius
	 * @param arcWidth
	 */
	private void recalculateShapes(double contentWidth, double contentHeight) {
		if(foregroundMask != null) {
			getChildren().remove(foregroundMask);
		}

		Rectangle fill = new Rectangle(contentWidth*2, contentHeight*2);
		fill.setX(-contentWidth);
		fill.setY(-contentHeight);

		foregroundMask = Shape.subtract(fill, arcMask());
		foregroundMask.setMouseTransparent(true);
		foregroundMask.setFill(Color.BLACK); // is clipped away
		foregroundMask.setEffect(shadow);
		foregroundMask.setClip(arcMask()); // do not draw shadow outside
		getChildren().add(foregroundMask);

		// Bar
		rebuildBar();

		// Ticks
		updateTicks();
	}

	private Shape arcMask() {
		return Shape.subtract(new Circle(barRadius+barWidth/2), new Circle(barRadius-barWidth/2));
	}

	@Override
    protected double computeMinWidth(double height,
            double topInset, double rightInset,
            double bottomInset, double leftInset) {
        return height;
    }

	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return width;
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return Math.max(width, getSkinnable().getPrefHeight());
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return Math.max(height, getSkinnable().getPrefWidth());
	}

    @Override
    protected void layoutChildren(double contentX, double contentY,
            double contentWidth, double contentHeight) {
    	barRadius = Math.min(contentWidth, contentHeight) * getSkinnable().getDiameter() / 2;
    	barWidth = Math.min(contentWidth, contentHeight) * getSkinnable().getThickness();
    	recalculateShapes(contentWidth, contentHeight);
    	layoutInArea(foregroundMask, contentX, contentY, contentWidth, contentHeight, 0, HPos.CENTER, VPos.CENTER);
    	centralGroup.setTranslateX(contentWidth/2);
    	centralGroup.setTranslateY(contentHeight/2);
    	centralScale.setX(barRadius);
    	centralScale.setY(barRadius);
    }
}
