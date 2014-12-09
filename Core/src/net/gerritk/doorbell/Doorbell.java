package net.gerritk.doorbell;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import net.gerritk.doorbell.events.DoorbellEvent;
import net.gerritk.doorbell.services.DoorbellService;
import net.gerritk.doorbell.services.ServiceContainer;

public class Doorbell implements GpioPinListenerDigital {
	private final String identifier;
	private final GpioPinDigitalInput input;
	private final GpioPinDigitalOutput output;
	private DoorbellService doorbellService;

	public Doorbell(String identifier, Pin input, Pin output) {
		this.identifier = identifier;

		GpioController gpio = GpioFactory.getInstance();

		this.input = gpio.provisionDigitalInputPin(input, PinPullResistance.PULL_DOWN);
		this.input.addListener(this);

		this.output = gpio.provisionDigitalOutputPin(output, PinState.LOW);
		System.out.println("INITIALIZED_DOORBELL::" + identifier);
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		System.out.println("DOORBELL::" + identifier + "::" + event.getState());
		if(event.getState() == PinState.HIGH) {
			if(doorbellService != null) {
				DoorbellService service = ServiceContainer.getService(DoorbellService.class);
				service.fireRinging(new DoorbellEvent("doorbell.ring", identifier, System.currentTimeMillis()));
			}
		}
	}

	public void blink() {
		output.blink(250, 750, PinState.HIGH);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setDoorbellService(DoorbellService doorbellService) {
		if(this.doorbellService != doorbellService) {
			if(this.doorbellService != null) {
				this.doorbellService.unregisterDoorbell(this);
			}

			if(doorbellService != null && !doorbellService.containsDoorbell(this)) {
				doorbellService.registerDoorbell(this);
			}

			this.doorbellService = doorbellService;
		}
	}

	public DoorbellService getDoorbellService() {
		return doorbellService;
	}
}
