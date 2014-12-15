package net.gerritk.homesystem.raspi.commands;

import net.gerritk.homesystem.commands.Command;
import net.gerritk.homesystem.raspi.RaspiHomesystem;

import java.util.Timer;
import java.util.TimerTask;

public class QuitCommand implements Command {
	private RaspiHomesystem homesystem;

	public QuitCommand(RaspiHomesystem homesystem) {
		if(homesystem == null) {
			throw new IllegalArgumentException("homesystem must not be null!");
		}
		this.homesystem = homesystem;
	}

	@Override
	public String getDefinition() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Stops the homesystem.";
	}

	@Override
	public String getUsage() {
		return getCommand();
	}

	@Override
	public String getCommand() {
		return "quit";
	}

	@Override
	public boolean execute(String[] args) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				homesystem.shutdown();
			}
		}, 100);
		return true;
	}
}
