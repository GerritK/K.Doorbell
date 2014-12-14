package net.gerritk.homesystem.raspi;

import com.pi4j.io.gpio.RaspiPin;
import net.gerritk.homesystem.events.DoorbellEvent;
import net.gerritk.homesystem.plugins.PluginManager;
import net.gerritk.homesystem.raspi.elements.RaspiDoorbell;
import net.gerritk.homesystem.raspi.services.RaspiStatusService;
import net.gerritk.homesystem.elements.DoorbellService;
import net.gerritk.homesystem.services.ServiceContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RaspiHomesystem {
	public RaspiHomesystem() {
		System.out.println("[RaspiHomesystem] Initializing...");
		initializeServices();
		initializeHardware();
		System.out.println("[RaspiHomesystem] Initialized!");

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
						doorbellService.onRinging(event);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		shutdown();
	}

	private void shutdown() {
		System.out.println("[RaspiHomesystem] Finishing...");
		ServiceContainer.getInstance().dispose();
		System.out.println("[RaspiHomesystem] Finished!");
		System.exit(0);
	}

	private void initializeHardware() {
		final DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);

		RaspiDoorbell doorbell = new RaspiDoorbell("gerrit", RaspiPin.GPIO_07, RaspiPin.GPIO_00);
		doorbellService.registerElement(doorbell);

		doorbell = new RaspiDoorbell("andrea", RaspiPin.GPIO_01, RaspiPin.GPIO_02);
		doorbellService.registerElement(doorbell);
	}

	private void initializeServices() {
		final ServiceContainer serviceContainer = ServiceContainer.getInstance();

		serviceContainer.add(new PluginManager("plugins"));
		serviceContainer.add(new DoorbellService());
		serviceContainer.add(new RaspiStatusService(RaspiPin.GPIO_03, RaspiPin.GPIO_04));

		serviceContainer.initialize();
	}

	public static void main(String[] args) {
		RaspiHomesystem homesystem = new RaspiHomesystem();
	}
}
