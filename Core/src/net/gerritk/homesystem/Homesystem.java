package net.gerritk.homesystem;

import net.gerritk.homesystem.commands.CommandService;
import net.gerritk.homesystem.commands.SimpleCommand;
import net.gerritk.homesystem.elements.DoorbellService;
import net.gerritk.homesystem.plugins.PluginManager;
import net.gerritk.homesystem.services.ServiceContainer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public abstract class Homesystem {
	public Homesystem() {
		RuntimeManager.setHomesystem(this);
		onCreateCore();
		onCreate();
		ServiceContainer.getInstance().initialize();
	}

	public abstract void onCreate();

	public abstract void onDestroy();

	private void onCreateCore() {
		final ServiceContainer container = ServiceContainer.getInstance();

		CommandService commandService = new CommandService();
		commandService.registerCommand(new SimpleCommand("quit", null, "Stops the homesystem.", "quit", new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						RuntimeManager.shutdown();
					}
				}, 100);
				return true;
			}
		}));
		container.add(commandService);

		container.add(new DoorbellService());
		container.add(new PluginManager("plugins"));
	}
}
