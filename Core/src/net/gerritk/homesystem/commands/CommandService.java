package net.gerritk.homesystem.commands;

import net.gerritk.homesystem.interfaces.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Vector;

public class CommandService implements Service, Runnable {
	private Runnable cmdListening;
	private Vector<Command> commands;

	public CommandService() {
		commands = new Vector<Command>();
	}

	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input;
		try {
			while((input = reader.readLine()) != null && cmdListening == this) {
				input = input.trim();

				if(!input.isEmpty() && input.startsWith("!")) {
					input = input.substring(1);

					String[] tmp = input.split(" ");
					String cmd = tmp[0];
					String[] args = Arrays.copyOfRange(tmp, 1, tmp.length);

					execute(cmd, args);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException ignore) { }

			cmdListening = null;
		}
	}

	public boolean execute(String command, String[] args) {
		if(command == null || (command = command.trim()).isEmpty()) {
			throw new IllegalArgumentException("command must not be null or empty!");
		}
		if(args == null) {
			args = new String[0];
		}

		boolean found = false;
		boolean executed = false;

		for(Command cmd : getCommands(command)) {
			if(cmd.getCommand().equals(command)) {
				found = true;
				executed = cmd.execute(args);

				if(executed) {
					break;
				}
			}
		}

		if(!found) {
			System.out.println("Command not found. Type '!help' to list all available commands.");
		} else if(!executed) {
			System.out.println("Invalid command arguments! Type '!help <command>' to get detailed help.");
		}

		return found && executed;
	}

	public Command getCommand(String command) {
		for(Command cmd : commands) {
			if(cmd.getCommand().equals(command)) {
				return cmd;
			}
		}
		return null;
	}

	public Command[] getCommands(String command) {
		Vector<Command> result = new Vector<Command>();
		for(Command cmd : commands) {
			if(cmd.getCommand().equals(command)) {
				result.add(cmd);
			}
		}
		return result.toArray(new Command[result.size()]);
	}

	public Command[] getCommands() {
		return commands.toArray(new Command[commands.size()]);
	}

	public void registerCommand(Command command) {
		if(commands.contains(command)) {
			System.out.println("[WARNING] The same command is already registered.");
			return;
		}
		if(getCommand(command.getCommand()) != null) {
			System.out.println("[WARNING] A command with the same command name '" + command.getCommand() + "' is already registered. It may result in errors!");
		}

		commands.add(command);
	}

	public void unregisterCommand(Command command) {
		commands.remove(command);
	}

	@Override
	public void initialize() {
		registerCommand(new HelpCommand());

		cmdListening = this;
		Thread cmdThread = new Thread(cmdListening);
		cmdThread.start();
	}

	@Override
	public void dispose() {
		commands.clear();
		cmdListening = null;
	}
}
