package net.gerritk.doorbell;

import net.gerritk.doorbell.events.DoorbellEvent;
import net.gerritk.doorbell.services.DoorbellService;
import net.gerritk.doorbell.plugins.PluginManager;
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
					} else if(cmd.startsWith("!ring")) {
						String[] args = cmd.split(" ");
						String identifier = null;

						for(int i = 1; i < args.length; i++) {
							if(args[i].equals("-id") && i + 1 < args.length) {
								i++;
								identifier = args[i];
							}
						}

						if(identifier != null) {
							DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);
							DoorbellEvent event = new DoorbellEvent("doorbell.ring", identifier, System.currentTimeMillis());
							doorbellService.fireRinging(event);
						}
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

		DoorbellService doorbellService = new DoorbellService();
		result = result && serviceContainer.add(doorbellService);
		doorbellService.initialize();

		return result;
	}

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("[Core] Finishing...");
				ServiceContainer.getInstance().dispose();
				System.out.println("[Core] Finished!");
			}
		}));

		new Main();
	}
}
