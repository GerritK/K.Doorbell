package net.gerritk.homesystem.services;

import net.gerritk.homesystem.interfaces.Disposable;
import net.gerritk.homesystem.interfaces.Service;

import java.util.Vector;

public class ServiceContainer implements Disposable {
	private static ServiceContainer instance;
	private Vector<Service> services;

	private ServiceContainer() {
		services = new Vector<Service>();
	}

	public void initialize() {
		for(Service service : services) {
			service.initialize();
		}
	}

	public <T extends Service> boolean add(T service) {
		return service != null && !services.contains(service) && services.add(service);
	}

	@SuppressWarnings("unchecked")
	public <T extends Service> T get(Class<T> type) {
		for(Service service : services) {
			if(type.isAssignableFrom(service.getClass())) {
				return (T) service;
			}
		}

		return null;
	}

	public static <T extends Service> T getService(Class<T> type) {
		return getInstance().get(type);
	}

	public static ServiceContainer getInstance() {
		if(instance == null) {
			synchronized (ServiceContainer.class) {
				if(instance == null) {
					instance = new ServiceContainer();
				}
			}
		}
		return instance;
	}

	@Override
	public void dispose() {
		instance = null;

		for(Service service : services) {
			service.dispose();
		}

		services = null;
	}
}
