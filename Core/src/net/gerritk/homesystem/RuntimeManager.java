package net.gerritk.homesystem;

import net.gerritk.homesystem.services.ServiceContainer;

public class RuntimeManager {
	private static Homesystem homesystem;

	private RuntimeManager() {
		// Only prevent instances!
	}

	public static void shutdown() {
		homesystem.onDestroy();
		ServiceContainer.getInstance().dispose();
		System.exit(0);
	}

	protected static void setHomesystem(Homesystem homesystem) {
		RuntimeManager.homesystem = homesystem;
	}
}
