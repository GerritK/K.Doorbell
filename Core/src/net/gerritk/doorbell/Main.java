package net.gerritk.doorbell;

import net.gerritk.doorbell.services.PluginManager;
import net.gerritk.doorbell.services.ServiceContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public Main() {
		System.out.println("[Core] Initializing...");
		boolean initResult = initializeServices();
		System.out.println("[Core] Initialized!");

		if(initResult) {
			System.out.println(">> Type '!quit' to stop execution. <<");

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String cmd;
			try {
				while((cmd = reader.readLine()) != null) {
					if(cmd.equals("!quit")) {
						break;
					} else if(cmd.equals("!help")) {
						System.out.println(">> Type '!quit' to stop execution. <<");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.exit(0);
	}

	private boolean initializeServices() {
		ServiceContainer serviceContainer = ServiceContainer.getInstance();

		PluginManager pluginManager = new PluginManager("plugins");
		boolean result = serviceContainer.add(pluginManager);

		pluginManager.initialize();

		return result;
	}

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("[Core] Finishing...");
				ServiceContainer.getInstance().dispose();
				System.out.println("[Core] Finished!");
				Thread.yield();
			}
		}));

		new Main();
	}
}
