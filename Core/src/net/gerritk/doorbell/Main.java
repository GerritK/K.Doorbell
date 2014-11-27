package net.gerritk.doorbell;

import net.gerritk.doorbell.services.PluginManager;
import net.gerritk.doorbell.services.ServiceContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException {
		ServiceContainer serviceContainer = ServiceContainer.getInstance();

		PluginManager pluginManager = new PluginManager("plugins");
		serviceContainer.add(pluginManager);

		pluginManager.initialize();

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String cmd;
		while((cmd = reader.readLine()) != null) {
			if(cmd.equals("!quit")) {
				break;
			}
		}

		serviceContainer.dispose();
	}
}
