package net.gerritk.doorbell;

import net.gerritk.doorbell.services.PluginManager;
import net.gerritk.doorbell.services.ServiceContainer;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		ServiceContainer serviceContainer = ServiceContainer.getInstance();

		PluginManager pluginManager = new PluginManager("plugins");
		serviceContainer.add(pluginManager);

		pluginManager.initialize();

		Thread.sleep(10000);

		serviceContainer.dispose();
	}
}
