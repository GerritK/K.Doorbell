package net.gerritk.homesystem.plugins;

import net.gerritk.homesystem.exceptions.InvalidDescriptionException;
import net.gerritk.homesystem.exceptions.InvalidPluginException;
import net.gerritk.homesystem.interfaces.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PluginManager implements Service {
	private PluginLoader pluginLoader;
	private final List<Plugin> plugins;
	private final File pluginDir;

	public PluginManager(String pluginDir) {
		File dir = new File(pluginDir);

		if(!dir.exists() || !dir.isDirectory()) {
			throw new IllegalArgumentException("plugin directory must be an existing directory!");
		}

		this.pluginDir = dir;
		this.plugins = new Vector<Plugin>();
		this.pluginLoader = new PluginLoader();
	}

	@Override
	public void initialize() {
		Map<String, File> plugins = new HashMap<String, File>();
		File[] files = pluginDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.isDirectory() && pathname.getPath().endsWith(".jar");
			}
		});

		for(File file : files) {
			PluginDescriptionFile description = null;
			try {
				description = pluginLoader.getPluginDescription(file);
			} catch (InvalidDescriptionException e) {
				System.err.println("[ERROR] Could not load '" + file.getPath() + "' in folder '" + pluginDir.getPath() + "': " + e.getMessage());
				continue;
			}

			File replacedFile = plugins.put(description.getName(), file);
			if(replacedFile != null) {
				System.out.println("[WARNING] Found more than one plugin with same name '" + description.getName() + "' in folder '" + pluginDir.getPath() + "' for file '" + file.getPath() + "' and '" + replacedFile.getPath() + "'");
			}
		}

		for(Map.Entry<String, File> entry : plugins.entrySet()) {
			Plugin plugin;
			try {
				plugin = pluginLoader.loadPlugin(entry.getValue());
			} catch (InvalidPluginException e) {
				System.err.println("[ERROR] Could not load plugin '" + entry.getKey() + "': " + e.getMessage());
				e.printStackTrace();
				continue;
			}

			this.plugins.add(plugin);
		}

		List<Plugin> tmpPlugins = new Vector<Plugin>(this.plugins);
		for(Plugin plugin : tmpPlugins) {
			boolean result = plugin.initialize();
			if(!result) {
				this.plugins.remove(plugin);
			}
		}
	}

	@Override
	public void dispose() {
		for(Plugin plugin : plugins) {
			plugin.dispose();
		}
	}
}
