package net.gerritk.homesystem.commands;

import net.gerritk.homesystem.services.ServiceContainer;

public class HelpCommand implements Command {
	@Override
	public String getDefinition() {
		return "[command]";
	}

	@Override
	public String getDescription() {
		return "Shows a list of available commands.";
	}

	@Override
	public String getUsage() {
		return "help | help quit";
	}

	@Override
	public String getCommand() {
		return "help";
	}

	@Override
	public boolean execute(String[] args) {
		final CommandService commandService = ServiceContainer.getService(CommandService.class);
		if(args.length > 0) {
			Command[] commands = commandService.getCommands(args[0]);
			printHelp(commands, true);
		} else {
			Command[] commands = commandService.getCommands();
			printHelp(commands, false);
		}
		return true;
	}

	private void printHelp(Command[] commands, boolean detailed) {
		System.out.println("--- HELP ---");

		if(commands != null) {
			for(Command command : commands) {
				if(detailed) {
					System.out.println(command.getCommand() + " " + command.getDefinition());
					System.out.println("Description: " + command.getDescription());
					System.out.println("Usage: " + command.getUsage());
				} else {
					System.out.println(command.getCommand() + " " + command.getDefinition());
				}
			}
		}

		System.out.println("--- HELP ---");
	}
}
