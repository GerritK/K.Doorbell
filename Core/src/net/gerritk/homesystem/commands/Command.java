package net.gerritk.homesystem.commands;

public interface Command {
	public String getDefinition();

	public String getDescription();

	public String getUsage();

	public String getCommand();

	public boolean execute(String[] args);
}
