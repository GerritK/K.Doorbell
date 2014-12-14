package net.gerritk.homesystem.elements;

import net.gerritk.homesystem.events.DoorbellEvent;
import net.gerritk.homesystem.events.DoorbellListener;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DoorbellService implements ElementService<Doorbell>, DoorbellListener {
	private Vector<DoorbellListener> listeners;
	private Vector<Doorbell> doorbells;
	private ExecutorService executor;

	@Override
	public void initialize() {
		listeners = new Vector<DoorbellListener>();
		doorbells = new Vector<Doorbell>();
		executor = Executors.newCachedThreadPool();
	}

	@Override
	public void dispose() {
		executor.shutdown();
		try {
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			executor = null;
		}
		listeners = null;
	}

	@Override
	public void registerElement(Doorbell doorbell) {
		if (doorbells.contains(doorbell)) {
			System.out.println("[WARNING] Doorbell '" + doorbell + "' already registered.");
			return;
		}

		doorbells.add(doorbell);
		doorbell.setService(this);
	}

	@Override
	public void unregisterElement(Doorbell doorbell) {
		doorbells.remove(doorbell);
		if(doorbell.getService() == this) {
			doorbell.setService(null);
		}
	}

	@Override
	public boolean isElementRegistered(Doorbell doorbell) {
		return doorbells.contains(doorbell);
	}

	public Doorbell getDoorbell(String id) {
		for(Doorbell doorbell : doorbells) {
			if(doorbell.getIdentifier().equals(id)) {
				return doorbell;
			}
		}

		return null;
	}

	@Override
	public void onRinging(DoorbellEvent event) {
		for(DoorbellListener listener : listeners) {
			listener.onRinging(event);
		}
	}

	public void registerListener(DoorbellListener listener) {
		if(listeners.contains(listener)) {
			System.out.println("[WARNING] Listener already registered.");
			return;
		}
		listeners.add(listener);
	}

	public void unregisterListener(DoorbellListener listener) {
		listeners.remove(listener);
	}
}
