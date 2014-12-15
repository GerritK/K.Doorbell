package net.gerritk.homesystem.commands;

import java.util.concurrent.Callable;

public class SimpleCommand implements Command {
	private final String command;
	private final Callable<Boolean> callable;

	private String definition;
	private String description;
	private String usage;

	public SimpleCommand(String command, String definition, String description, String usage, Callable<Boolean> callable) {
		if(command == null || (command = command.trim()).isEmpty()) {
			throw new IllegalArgumentException("command must not be null or empty!");
		}
		if(callable == null) {
			throw new IllegalArgumentException("callable must not be null!");
		}
		if(definition == null) {
			definition = "";
		}
		if(description == null) {
			description = "Missing description!";
		}
		if(usage == null) {
			usage = "Missing usage!";
		}

		this.command = command;
		this.callable = callable;
		this.definition = definition;
		this.description = description;
		this.usage = usage;
	}

	public SimpleCommand(String command, Callable<Boolean> callable) {
		this(command, null, null, command, callable);
	}

	@Override
	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		if(definition == null) {
			definition = "";
		}
		this.definition = definition;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if(description == null) {
			description = "Missing description!";
		}
		this.description = description;
	}

	@Override
	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		if(usage == null) {
			usage = "Missing usage!";
		}
		this.usage = usage;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public boolean execute(String[] args) {
		try {
			return callable.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
