package net.gerritk.homesystem.raspi;

import com.pi4j.io.gpio.RaspiPin;
import net.gerritk.homesystem.Homesystem;
import net.gerritk.homesystem.elements.DoorbellService;
import net.gerritk.homesystem.raspi.elements.RaspiDoorbell;
import net.gerritk.homesystem.raspi.services.RaspiStatusService;
import net.gerritk.homesystem.services.ServiceContainer;

public class RaspiHomesystem extends Homesystem {

	@Override
	public void onCreate() {
		System.out.println("[RaspiHomesystem] Initializing...");
		initializeServices();
		initializeHardware();
		System.out.println("[RaspiHomesystem] Initialized!");
	}

	@Override
	public void onDestroy() {
		System.out.println("[RaspiHomesystem] Finishing...");
		// Add something...
		System.out.println("[RaspiHomesystem] Finished!");
	}

	// TODO only demo!
	private void initializeHardware() {
		final DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);

		RaspiDoorbell doorbell = new RaspiDoorbell("gerrit", RaspiPin.GPIO_07, RaspiPin.GPIO_00);
		doorbellService.registerElement(doorbell);

		doorbell = new RaspiDoorbell("andrea", RaspiPin.GPIO_01, RaspiPin.GPIO_02);
		doorbellService.registerElement(doorbell);
	}

	private void initializeServices() {
		final ServiceContainer container = ServiceContainer.getInstance();

		container.add(new RaspiStatusService(RaspiPin.GPIO_03, RaspiPin.GPIO_04));
	}

	public static void main(String[] args) {
		Homesystem homesystem = new RaspiHomesystem();
	}
}
