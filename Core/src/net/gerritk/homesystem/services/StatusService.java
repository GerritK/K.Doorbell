package net.gerritk.homesystem.services;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import net.gerritk.homesystem.enums.ServerState;
import net.gerritk.homesystem.interfaces.Service;

public class StatusService implements Service, GpioPinListenerDigital{
	private GpioPinDigitalInput reset;
	private GpioPinDigitalOutput output;
	private ServerState state;

	public StatusService(Pin reset, Pin output) {
		GpioController gpio = GpioFactory.getInstance();

		this.reset = gpio.provisionDigitalInputPin(reset, PinPullResistance.PULL_DOWN);
		this.reset.addListener(this);

		this.output = gpio.provisionDigitalOutputPin(output);
		this.output.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
	}

	public void setState(ServerState state, int code) {
		this.state = state;
		switch (state) {
			case ERROR:
				System.err.println("[ERROR] State::" + code);
				output.blink(200);
				break;
			case WARNING:
				System.out.println("[WARNING] State::" + code);
				output.blink(500);
				break;
			case INFO:
				System.out.println("[INFO] State::" + code);
				output.blink(1000);
				break;
			case OKAY:
				System.out.println("[STATE] OKAY::" + code);
				output.setState(true);
				break;
		}
	}

	@Override
	public void initialize() {
		setState(ServerState.OKAY, 0);
	}

	@Override
	public void dispose() {

	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		if(event.getState() == PinState.HIGH) {
			switch (state) {
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
