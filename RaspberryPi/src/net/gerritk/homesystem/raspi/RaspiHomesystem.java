package net.gerritk.homesystem.raspi;

import com.pi4j.io.gpio.RaspiPin;
import net.gerritk.homesystem.commands.CommandService;
import net.gerritk.homesystem.elements.DoorbellService;
import net.gerritk.homesystem.plugins.PluginManager;
import net.gerritk.homesystem.raspi.commands.QuitCommand;
import net.gerritk.homesystem.raspi.elements.RaspiDoorbell;
import net.gerritk.homesystem.raspi.services.RaspiStatusService;
import net.gerritk.homesystem.services.ServiceContainer;

public class RaspiHomesystem {
	public RaspiHomesystem() {
		System.out.println("[RaspiHomesystem] Initializing...");
		initializeServices();
		initializeHardware();
		System.out.println("[RaspiHomesystem] Initialized!");

		System.out.println(">> Type '!quit' to stop execution. <<");
	}

	public void shutdown() {
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

		CommandService commandService = new CommandService();
		commandService.registerCommand(new QuitCommand(this));
		serviceContainer.add(commandService);

		serviceContainer.add(new DoorbellService());
		serviceContainer.add(new RaspiStatusService(RaspiPin.GPIO_03, RaspiPin.GPIO_04));

		serviceContainer.add(new PluginManager("plugins"));

		serviceContainer.initialize();
	}

	public static void main(String[] args) {
		RaspiHomesystem homesystem = new RaspiHomesystem();
	}
}
