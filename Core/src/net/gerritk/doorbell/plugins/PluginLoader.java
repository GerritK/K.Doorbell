package net.gerritk.doorbell.plugins;

import net.gerritk.doorbell.exceptions.InvalidDescriptionException;
import net.gerritk.doorbell.exceptions.InvalidPluginException;
import net.minidev.json.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
	private final Map<String, Class<?>> classes;
	private final Map<String, PluginClassLoader> loaders;

	public PluginLoader() {
		classes = new HashMap<String, Class<?>>();
		loaders = new LinkedHashMap<String, PluginClassLoader>();
	}

	public Plugin loadPlugin(final File file) throws InvalidPluginException {
		if (file == null || !file.exists()) {
			throw new IllegalArgumentException("file must not be null and must exist!");
		}

		final PluginDescriptionFile description;
		try {
			description = getPluginDescription(file);
		} catch (InvalidDescriptionException e) {
			throw new InvalidPluginException(e);
		}

		final PluginClassLoader loader;
		try {
			loader = new PluginClassLoader(this, getClass().getClassLoader(), description, file);
		} catch (InvalidPluginException e) {
			throw e;
		} catch (Throwable e) {
			throw new InvalidPluginException(e);
		}

		loaders.put(description.getName(), loader);

		return loader.plugin;
	}

	public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
		if(file == null || !file.exists()) {
			throw new IllegalArgumentException("file must not be null and must exist!");
		}

		JarFile jar = null;
		InputStream stream = null;

		try {
			jar = new JarFile(file);
			JarEntry entry = jar.getJarEntry("plugin.json");

			if(entry == null) {
				throw new InvalidDescriptionException(new FileNotFoundException("jar does not contain plugin.json"));
			}

			stream = jar.getInputStream(entry);

			return new PluginDescriptionFile(stream);
		} catch (IOException e) {
			throw new InvalidDescriptionException(e);
		} catch (ParseException e) {
			throw new InvalidDescriptionException(e);
		} finally {
			if(jar != null) {
				try {
					jar.close();
				} catch (IOException ignore) {
				}
			}
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	Class<?> getClassByName(final String name) {
		Class<?> cachedClass = classes.get(name);

		if(cachedClass != null) {
			return cachedClass;
		} else {
			for(String current : loaders.keySet()) {
				PluginClassLoader loader = loaders.get(current);

				try {
					cachedClass = loader.findClass(name, false);
				} catch (ClassNotFoundException ignore) {
				}
				if(cachedClass != null) {
					return cachedClass;
				}
			}
		}

		return null;
	}

	void setClass(final String name, final Class<?> clazz) {
		if(classes.containsKey(name)) {
			classes.put(name, clazz);
		}
	}

	private void removeClass(String name) {
		classes.remove(name);
	}
}
