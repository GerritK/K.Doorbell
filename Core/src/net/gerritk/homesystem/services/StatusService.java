package net.gerritk.homesystem.services;

import net.gerritk.homesystem.interfaces.Service;
import net.gerritk.homesystem.enums.ServerState;

public abstract class StatusService implements Service {
	private ServerState state = ServerState.OKAY;
	private int code;

	public void setState(ServerState state, int code) {
		this.state = state;
		switch (state) {
			case ERROR:
				System.err.println("[ERROR] State::" + code);
				break;
			case WARNING:
				System.out.println("[WARNING] State::" + code);
				break;
			case INFO:
				System.out.println("[INFO] State::" + code);
				break;
			case OKAY:
				System.out.println("[STATE] OKAY::" + code);
				break;
		}
	}

	public ServerState getState() {
		return state;
	}

	public int getCode() {
		return code;
	}

	@Override
	public void initialize() {
		setState(ServerState.OKAY, 0);
	}

	@Override
	public void dispose() {

	}
}
