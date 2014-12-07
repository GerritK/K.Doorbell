package net.gerritk.doorbell.plugins;

import net.gerritk.doorbell.exceptions.InvalidPluginException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PluginClassLoader extends URLClassLoader {
	private final PluginLoader loader;
	private final Map<String, Class<?>> classes;
	private final PluginDescriptionFile description;
	private final File file;
	final Plugin plugin;
	private Plugin pluginInit;

	public PluginClassLoader(final PluginLoader loader, final ClassLoader parent, final PluginDescriptionFile description, final File file) throws MalformedURLException, InvalidPluginException {
		super(new URL[]{file.toURI().toURL()}, parent);

		if(loader == null) {
			throw new IllegalArgumentException("Loader must not be null!");
		}

		this.classes = new HashMap<String, Class<?>>();
		this.loader = loader;
		this.description = description;
		this.file = file;

		try {
			Class<?> jarClass;
			try {
				jarClass = Class.forName(description.getMain(), true, this);
			} catch (ClassNotFoundException e) {
				throw new InvalidPluginException("cannot find main class '" + description.getMain() + "'", e);
			}

			Class<? extends Plugin> pluginClass;
			try {
				pluginClass = jarClass.asSubclass(Plugin.class);
			} catch (ClassCastException e) {
				throw new InvalidPluginException("main class '" + description.getMain() + "' does not extend plugin", e);
			}

			plugin = pluginClass.newInstance();
		} catch (InstantiationException e) {
			throw new InvalidPluginException("abnormal plugin type", e);
		} catch (IllegalAccessException e) {
			throw new InvalidPluginException("no public constructor", e);
		}
	}

	Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
		Class<?> result = classes.get(name);

		if(result == null) {
			if(checkGlobal) {
				result = loader.getClassByName(name);
			}

			if(result == null) {
				result = super.findClass(name);

				if(result != null) {
					loader.setClass(name, result);
				}
			}

			classes.put(name, result);
		}

		return result;
	}

	Set<String> getClasses() {
		return classes.keySet();
	}

	synchronized void initialize(Plugin plugin) {
		if(plugin == null) {
			throw new IllegalArgumentException("Initializing plugin must not be null!");
		}
		if(plugin.getClass().getClassLoader() != this) {
			throw new IllegalArgumentException("Cannot initialize plugin outside of this class loader");
		}
		if(this.plugin != null || this.pluginInit != null) {
			throw new IllegalArgumentException("Plugin already initialized!");
		}

		this.pluginInit = plugin;

		plugin.init(loader, description, file, this);
	}
}
