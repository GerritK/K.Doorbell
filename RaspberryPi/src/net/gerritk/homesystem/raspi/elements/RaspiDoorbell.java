package net.gerritk.homesystem.raspi.elements;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import net.gerritk.homesystem.elements.Doorbell;
import net.gerritk.homesystem.events.DoorbellEvent;

public class RaspiDoorbell extends Doorbell implements GpioPinListenerDigital {
	private final static long BLINK_DURATION = 500;

	private final GpioPinDigitalInput input;
	private final GpioPinDigitalOutput output;

	public RaspiDoorbell(String identifier, Pin input, Pin output) {
		super(identifier);

		GpioController gpio = GpioFactory.getInstance();

		this.input = gpio.provisionDigitalInputPin(input, PinPullResistance.PULL_DOWN);
		this.input.addListener(this);

		this.output = gpio.provisionDigitalOutputPin(output, PinState.LOW);
		this.output.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
	}

	@Override
	public void setOutput(boolean enabled) {
		if(enabled) {
			output.blink(BLINK_DURATION, PinState.HIGH);
		} else {
			output.setState(false);
		}
	}

	@Override
	public void setOutput(long duration) {
		int times = (int) (duration / BLINK_DURATION);
		if(times % 2 == 0) {
			times += 1;
		}
		output.blink(250, times * BLINK_DURATION - 5, PinState.HIGH);
	}

	@Override
	public String toString() {
		String result = super.toString();
		return result.replace("@", " >in:" + input.getName() + " >out:" + output.getName() + " @");
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		if(event.getState() == PinState.LOW) {
			System.out.println("[Doorbell] '" + this + "' is ringing.");
			fireEvent(new DoorbellEvent("doorbell.ring", getIdentifier(), System.currentTimeMillis()));
		}
	}
}
