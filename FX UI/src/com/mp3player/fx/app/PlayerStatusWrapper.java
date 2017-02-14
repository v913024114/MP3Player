package com.mp3player.fx.app;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

import com.mp3player.vdp.Distributed;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import mp3player.player.PlayerStatus;

public class PlayerStatusWrapper {
	private PlayerStatus status;

	private DistributedDoubleProperty duration;
	public double getDuration() { return duration.get(); }
	public void setDuration(double value) { duration.set(value); }
	public DoubleProperty durationProperty() { return duration; }

	private DistributedDoubleProperty position;
	public double getPosition() { return position.get(); }
	public void setPosition(double value) { position.set(value); }
	public DoubleProperty positionProperty() { return position; }

	private BooleanProperty playing;
	public boolean isPlaying() { return playing.get(); }
	public void setPlaying(boolean value) { playing.set(value); }
	public BooleanProperty playingProperty() { return playing; }


	public PlayerStatusWrapper(PlayerStatus status) {
		this.status = status;

		playing = new DistributedBooleanProperty("playing", this,
				status.getPlayback(),
				() -> status.getPlayback().isPlaying(),
				newValue -> status.getTarget().setTargetPlaying(newValue));
		duration = new DistributedDoubleProperty("duration", this,
				status.getPlayback(),
				() -> status.getPlayback().getDuration(),
				newValue -> { throw new UnsupportedOperationException("cannot set duration"); });
		position = new DistributedDoubleProperty("position", this,
				status.getPlayback(),
				() -> status.getPlayback().getCurrentPosition(),
				newValue -> status.getTarget().setTargetPosition(newValue)) {
			@Override
			public double get() {
				return status.getPlayback().getCurrentPosition();
			}
		};

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			if(isPlaying()) {
				Platform.runLater(() -> position.invalidated());
			}
		}, 50, 50, TimeUnit.MILLISECONDS);
	}


	public PlayerStatus getStatus() {
		return status;
	}


	private static class DistributedDoubleProperty extends DoubleProperty
	{
		private String name;
		private Object bean;

		private Distributed distributed;
		private DoubleSupplier getter;
		private DoubleConsumer setter;
		private double lastValue;

		private List<ChangeListener<? super Number>> changeListeners = new CopyOnWriteArrayList<>();
		private List<InvalidationListener> invalidationListeners = new CopyOnWriteArrayList<>();


		public DistributedDoubleProperty(String name, Object bean, Distributed distributed, DoubleSupplier getter,
				DoubleConsumer setter) {
			this.name = name;
			this.bean = bean;
			this.distributed = distributed;
			this.getter = getter;
			this.setter = setter;
			lastValue = get();
			register();
		}

		private void register() {
			distributed.addDataChangeListener(e -> invalidated());
		}

		protected void invalidated() {
			double newValue = getter.getAsDouble();
			if(newValue == lastValue) return;
			double oldValue = lastValue;
			lastValue = newValue;
			fireChangedInvalidated(oldValue, newValue);
		}

		protected void fireChangedInvalidated(double oldValue, double newValue) {
			for(ChangeListener<? super Number> l : changeListeners) {
				l.changed(this, oldValue, newValue);
			}
			for(InvalidationListener l : invalidationListeners) {
				l.invalidated(this);
			}
		}


		@Override
		public void bind(ObservableValue<? extends Number> observable) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void unbind() {
			return;
		}
		@Override
		public boolean isBound() {
			return false;
		}
		@Override
		public Object getBean() {
			return bean;
		}
		@Override
		public String getName() {
			return name;
		}
		@Override
		public void addListener(ChangeListener<? super Number> listener) {
			changeListeners.add(listener);
		}
		@Override
		public void removeListener(ChangeListener<? super Number> listener) {
			changeListeners.remove(listener);
		}
		@Override
		public void addListener(InvalidationListener listener) {
			invalidationListeners.add(listener);
		}
		@Override
		public void removeListener(InvalidationListener listener) {
			invalidationListeners.remove(listener);
		}
		@Override
		public double get() {
			return lastValue;
		}
		@Override
		public void set(double value) {
			setter.accept(value);
		}
	}


	private static class DistributedBooleanProperty extends BooleanProperty
	{
		private String name;
		private Object bean;

		private Distributed distributed;
		private BooleanSupplier getter;
		private Consumer<Boolean> setter;
		private boolean lastValue;

		private List<ChangeListener<? super Boolean>> changeListeners = new CopyOnWriteArrayList<>();
		private List<InvalidationListener> invalidationListeners = new CopyOnWriteArrayList<>();


		public DistributedBooleanProperty(String name, Object bean, Distributed distributed, BooleanSupplier getter,
				Consumer<Boolean> setter) {
			this.name = name;
			this.bean = bean;
			this.distributed = distributed;
			this.getter = getter;
			this.setter = setter;
			lastValue = get();
			register();
		}

		private void register() {
			distributed.addDataChangeListener(e -> invalidated());
		}

		protected void invalidated() {
			boolean newValue = getter.getAsBoolean();
			if(newValue == lastValue) return;
			boolean oldValue = lastValue;
			lastValue = newValue;
			fireChangedInvalidated(oldValue, newValue);
		}

		protected void fireChangedInvalidated(boolean oldValue, boolean newValue) {
			for(ChangeListener<? super Boolean> l : changeListeners) {
				l.changed(this, oldValue, newValue);
			}
			for(InvalidationListener l : invalidationListeners) {
				l.invalidated(this);
			}
		}


		@Override
		public void bind(ObservableValue<? extends Boolean> observable) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void unbind() {
			return;
		}
		@Override
		public boolean isBound() {
			return false;
		}
		@Override
		public Object getBean() {
			return bean;
		}
		@Override
		public String getName() {
			return name;
		}
		@Override
		public void addListener(ChangeListener<? super Boolean> listener) {
			changeListeners.add(listener);
		}
		@Override
		public void removeListener(ChangeListener<? super Boolean> listener) {
			changeListeners.remove(listener);
		}
		@Override
		public void addListener(InvalidationListener listener) {
			invalidationListeners.add(listener);
		}
		@Override
		public void removeListener(InvalidationListener listener) {
			invalidationListeners.remove(listener);
		}
		@Override
		public boolean get() {
			return lastValue;
		}
		@Override
		public void set(boolean value) {
			setter.accept(value);
		}
	}
}
