package net.gerritk.doorbell.services;

import net.gerritk.doorbell.interfaces.DoorbellPlugin;
import net.gerritk.doorbell.interfaces.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class PluginManager implements Service {
	private File pluginDir;
	private HashMap<DoorbellPlugin, Boolean> plugins;
	private boolean initialized;

	public PluginManager(String path) {
		if(path == null || path.trim().isEmpty()) {
			throw new IllegalArgumentException("path must not be null or empty!");
		}

		this.plugins = new HashMap<DoorbellPlugin, Boolean>();
		this.pluginDir = new File(path);
	}

	@Override
	public void initialize() {
		if(initialized) {
			System.err.println("[PluginManager] Already initialized!");
			return;
		}
		initialized = true;

		File[] files = pluginDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getPath().toLowerCase().endsWith(".jar");
			}
		});

		if(files != null) {
			for(File pluginFile : files) {
				try {
					JarFile pluginJar = new JarFile(pluginFile);
					String mainClass = pluginJar.getManifest().getMainAttributes().getValue("Plugin-Class");

					Class clazz = new URLClassLoader(new URL[]{pluginFile.toURI().toURL()}).loadClass(mainClass);
					Class[] interfaces = clazz.getInterfaces();

					boolean isPlugin = false;
					for (Class anInterface : interfaces) {
						if (anInterface.equals(DoorbellPlugin.class)) {
							isPlugin = true;
							break;
						}
					}

					if(isPlugin) {
						DoorbellPlugin plugin = (DoorbellPlugin) clazz.newInstance();
						plugins.put(plugin, false);
					}
				} catch (IOException e) {
					e.printStackTrace();
					initialized = false;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					initialized = false;
				} catch (InstantiationException e) {
					e.printStackTrace();
					initialized = false;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					initialized = false;
				}
			}
		}

		for(Map.Entry<DoorbellPlugin, Boolean> entry : plugins.entrySet()) {
			try {
				boolean result = entry.getKey().initialize();
				plugins.put(entry.getKey(), result);
			} catch (Exception e) {
				System.err.println("[PluginManager] Initialize Exception: " + entry.getKey());
				e.printStackTrace();
			}
		}
	}

	public boolean existsPlugin(String pluginName) {
		for(Map.Entry<DoorbellPlugin, Boolean> entry : plugins.entrySet()) {
			if(pluginName.equals(entry.getKey().toString())) {
				return true;
			}
		}
		return false;
	}

	public boolean isPluginInitialized(String pluginName) {
		for(Map.Entry<DoorbellPlugin, Boolean> entry : plugins.entrySet()) {
			if(pluginName.equals(entry.getKey().toString())) {
				return entry.getValue();
			}
		}
		return false;
	}

	@Override
	public void dispose() {
		if(plugins == null) {
			System.err.println("[PluginManager] Already disposed!");
			return;
		}

		for(Map.Entry<DoorbellPlugin, Boolean> entry : plugins.entrySet()) {
			try {
				entry.getKey().dispose();
			} catch (Exception e) {
				System.err.println("[PluginManager] Dispose Exception: " + entry.getKey());
				e.printStackTrace();
			}
		}
		plugins = null;
	}
}
