package net.gerritk.homesystem.raspi.services;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import net.gerritk.homesystem.enums.ServerState;
import net.gerritk.homesystem.services.StatusService;

public class RaspiStatusService extends StatusService implements GpioPinListenerDigital {
	private GpioPinDigitalInput reset;
	private GpioPinDigitalOutput output;

	public RaspiStatusService(Pin reset, Pin output) {
		GpioController gpio = GpioFactory.getInstance();

		this.reset = gpio.provisionDigitalInputPin(reset, PinPullResistance.PULL_DOWN);
		this.reset.addListener(this);

		this.output = gpio.provisionDigitalOutputPin(output);
		this.output.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
	}

	@Override
	public void setState(ServerState state, int code) {
		super.setState(state, code);

		switch (state) {
			case ERROR:
				output.blink(250, PinState.HIGH);
				break;
			case WARNING:
				output.blink(500, PinState.HIGH);
				break;
			case INFO:
				output.blink(1000, PinState.HIGH);
				break;
			default:
			case OKAY:
				output.blink(0);
				output.setState(true);
				break;
		}
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		if(event.getState() == PinState.LOW) {
			switch (getState()) {
				case ERROR:
					setState(ServerState.WARNING, 0);
					break;
				case WARNING:
					setState(ServerState.INFO, 0);
					break;
				case INFO:
					setState(ServerState.OKAY, 0);
					break;
				default:
				case OKAY:
					setState(ServerState.ERROR, 0);
					break;
			}
		}
	}
}
