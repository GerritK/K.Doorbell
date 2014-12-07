package net.gerritk.doorbell.plugins;

import net.gerritk.doorbell.interfaces.Disposable;

import java.io.File;

public abstract class Plugin implements Disposable {
	private PluginLoader loader;
	private File file;
	private PluginDescriptionFile description;
	private ClassLoader classLoader;

	public Plugin() {
		final ClassLoader classLoader = this.getClass().getClassLoader();
		if(!(classLoader instanceof PluginClassLoader)) {
			throw new IllegalStateException("Plugin requires " + PluginClassLoader.class.getName());
		}
		((PluginClassLoader) classLoader).initialize(this);
	}

	protected Plugin(final PluginLoader loader, final PluginDescriptionFile description, final File file) {
		final ClassLoader classLoader = this.getClass().getClassLoader();
		if(classLoader instanceof PluginClassLoader) {
			throw new IllegalStateException("Cannot use initialization constructor at runtime");
		}
		init(loader, description, file, classLoader);
	}

	public final PluginLoader getPluginLoader() {
		return loader;
	}

	public final PluginDescriptionFile getDescription() {
		return description;
	}

	protected File getFile() {
		return file;
	}

	protected final ClassLoader getClassLoader() {
		return classLoader;
	}

	final void init(PluginLoader loader, PluginDescriptionFile description, File file, ClassLoader classLoader) {
		this.loader = loader;
		this.file = file;
		this.description = description;
		this.classLoader = classLoader;
	}

	public abstract boolean initialize();

	@Override
	public String toString() {
		return description.getFullName();
	}

	public static <T extends Plugin> T getPlugin(Class<T> clazz) {
		if(clazz == null) {
			throw new IllegalArgumentException("class must not be null!");
		}
		if(Plugin.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException(clazz + " does not extend " + Plugin.class);
		}

		final ClassLoader cl = clazz.getClassLoader();
		if(!(cl instanceof PluginClassLoader)) {
			throw new IllegalArgumentException(clazz + " is not initialized by " + PluginClassLoader.class);
		}

		Plugin plugin = ((PluginClassLoader) cl).plugin;
		if(plugin == null) {
			throw new IllegalStateException("Cannot get plugin for " + clazz + " from a static initializer");
		}
		return clazz.cast(plugin);
	}

	public static Plugin getProvidingPlugin(Class<?> clazz) {
		if(clazz == null) {
			throw new IllegalArgumentException("class must not be null");
		}

		final ClassLoader cl = clazz.getClassLoader();
		if(!(cl instanceof PluginClassLoader)) {
			throw new IllegalArgumentException(clazz + " is not provided by " + PluginClassLoader.class);
		}

		Plugin plugin = ((PluginClassLoader) cl).plugin;
		if(plugin == null) {
			throw new IllegalArgumentException("Cannot get plugin for " + clazz + " from a static initializer");
		}
		return plugin;
	}
}
