package net.gerritk.doorbell.services;

import net.gerritk.doorbell.Doorbell;
import net.gerritk.doorbell.events.DoorbellEvent;
import net.gerritk.doorbell.events.DoorbellListener;
import net.gerritk.doorbell.interfaces.Service;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DoorbellService implements Service {
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

	public void registerDoorbell(Doorbell doorbell) {
		if (doorbells.contains(doorbell)) {
			System.out.println("[WARNING] Doorbell already registered.");
			return;
		}

		doorbells.add(doorbell);
		doorbell.setDoorbellService(this);
	}

	public void unregisterDoorbell(Doorbell doorbell) {
		doorbells.remove(doorbell);
		if(doorbell.getDoorbellService() == this) {
			doorbell.setDoorbellService(null);
		}
	}

	public boolean containsDoorbell(Doorbell doorbell) {
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

	public void fireRinging(final DoorbellEvent event) {
		for(final DoorbellListener listener : listeners) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					listener.onRinging(event);
				}
			});
		}
	}
}
