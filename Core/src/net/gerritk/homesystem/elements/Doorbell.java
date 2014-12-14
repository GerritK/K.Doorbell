package net.gerritk.homesystem.elements;

import net.gerritk.homesystem.events.DoorbellEvent;
import net.gerritk.homesystem.events.DoorbellListener;

import java.util.Vector;

public abstract class Doorbell implements Element<Doorbell> {
	private final String identifier;
	private final Vector<DoorbellListener> listeners;

	private ElementService<Doorbell> doorbellService;

	public Doorbell(String identifier) {
		if(identifier == null || identifier.trim().isEmpty()) {
			throw new IllegalArgumentException("identifier must not be null or empty!");
		}

		this.identifier = identifier;
		this.listeners = new Vector<DoorbellListener>();
	}

	public void fireEvent(DoorbellEvent event) {
		for(DoorbellListener listener : listeners) {
			listener.onRinging(event);
		}
	}

	public abstract void setOutput(boolean enabled);

	public abstract void setOutput(long duration);

	public final String getIdentifier() {
		return identifier;
	}

	@Override
	public final ElementService<Doorbell> getService() {
		return doorbellService;
	}

	@Override
	public void setService(ElementService<Doorbell> elementService) {
		if(doorbellService != elementService) {
			if(doorbellService != null) {
				doorbellService.unregisterElement(this);

				if(this.doorbellService instanceof DoorbellListener) {
					unregisterListener((DoorbellListener) doorbellService);
				}
			}

			if(elementService != null) {
				if(!elementService.isElementRegistered(this)) {
					elementService.registerElement(this);
				}
				doorbellService = elementService;

				if(elementService instanceof DoorbellListener) {
					registerListener((DoorbellListener) elementService);
				}
			} else {
				doorbellService = null;
			}
		}
	}

	public void registerListener(DoorbellListener listener) {
		if (listeners.contains(listener)) {
			System.out.println("[WARNING] Listener already registered.");
			return;
		}

		listeners.add(listener);
	}

	public void unregisterListener(DoorbellListener listener) {
		listeners.remove(listener);
	}

	@Override
	public int hashCode() {
		return getIdentifier().hashCode();
	}

	@Override
	public String toString() {
		return identifier + "@" + getClass().getSimpleName();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Doorbell) {
			Doorbell element = (Doorbell) obj;
			return getIdentifier().equals(element.getIdentifier());
		}
		return false;
	}
}
