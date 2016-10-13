package com.mp3player.vdp;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * All objects that are shared among clients extend <code>Distributed</code>.
 * @author Philipp Holl
 *
 */
public abstract class Distributed implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String id;
	private transient List<Consumer<DataChangeEvent>> changeListeners = new CopyOnWriteArrayList<>();
	VDP vdp;


	public Distributed(String id) {
		this.id = id;
	}


	public abstract Distributed resolveConflict(Distributed other);


	void fireChangedExternally(DataChangeEvent e) {
		for(Consumer<DataChangeEvent> l : changeListeners) l.accept(e);
	}

	protected void fireChangedLocally() {
		if(vdp != null) vdp.changed(this);
	}

	public void addDataChangeListener(Consumer<DataChangeEvent> l) {
		changeListeners.add(l);
	}

	public void removeDataChangeListener(Consumer<DataChangeEvent> l) {
		changeListeners.remove(l);
	}

	public String getID() {
		return id;
	}

	void copyListenersFrom(Distributed other) {
		changeListeners = new CopyOnWriteArrayList<>(other.changeListeners);
	}

	void copyNonTransientFieldsFrom(Distributed other) throws IllegalArgumentException, IllegalAccessException {
		for(Field field : getClass().getFields()) {
			if(!field.isAccessible()) field.setAccessible(true);
			if(!Modifier.isTransient(field.getModifiers())) {
				Object value = field.get(other);
				field.set(this, value);
			}
		}
	}
}
