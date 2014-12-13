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
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		if(event.getState() == PinState.LOW) {
			if(doorbellService != null) {
				System.out.println("[Doorbell] '" + this + "' is ringing.");
				DoorbellService service = ServiceContainer.getService(DoorbellService.class);
				service.fireRinging(new DoorbellEvent("doorbell.ring", identifier, System.currentTimeMillis()));
			}
		}
	}

	@Override
	public String toString() {
		return identifier + "@in:" + input.getName() + ";out:" + output.getName();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Doorbell) {
			Doorbell d = (Doorbell) obj;
			return this.getIdentifier().equals(d.getIdentifier());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getIdentifier().hashCode();
	}

	public void blink() {
		final long duration = 250;
		output.blink(duration, 250 * (5 * 2 - 1) - 5, PinState.HIGH);
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
