package net.gerritk.homesystem;

import com.pi4j.io.gpio.RaspiPin;
import net.gerritk.homesystem.enums.ServerState;
import net.gerritk.homesystem.events.DoorbellEvent;
import net.gerritk.homesystem.plugins.PluginManager;
import net.gerritk.homesystem.services.DoorbellService;
import net.gerritk.homesystem.services.ServiceContainer;
import net.gerritk.homesystem.services.StatusService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public Main() {
		System.out.println("[Core] Initializing...");
		boolean initResult = initializeServices();
		initializeHardware();
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
							DoorbellEvent event = new DoorbellEvent("homesystem.ring", identifier, System.currentTimeMillis());
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

	private void initializeHardware() {
		final DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);

		Doorbell doorbell = new Doorbell("gerrit", RaspiPin.GPIO_07, RaspiPin.GPIO_00);
		doorbellService.registerDoorbell(doorbell);

		doorbell = new Doorbell("andrea", RaspiPin.GPIO_01, RaspiPin.GPIO_02);
		doorbellService.registerDoorbell(doorbell);

		final StatusService statusService = new StatusService(RaspiPin.GPIO_03, RaspiPin.GPIO_04);
		ServiceContainer.getInstance().add(statusService);
		statusService.setState(ServerState.OKAY, 0);
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
