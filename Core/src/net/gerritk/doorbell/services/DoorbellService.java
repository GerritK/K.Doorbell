package net.gerritk.doorbell.services;

import net.gerritk.doorbell.events.DoorbellEvent;
import net.gerritk.doorbell.events.DoorbellListener;
import net.gerritk.doorbell.interfaces.Service;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DoorbellService implements Service {
	private Vector<DoorbellListener> listeners;
	private ExecutorService executor;

	@Override
	public void initialize() {
		listeners = new Vector<DoorbellListener>();
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

	public void registerListener(DoorbellListener listener) {
		if(listeners.contains(listener)) {
			System.out.println("[DoorbellService] Listener already registered.");
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
