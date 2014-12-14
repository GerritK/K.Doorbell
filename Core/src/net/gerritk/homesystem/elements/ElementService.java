package net.gerritk.homesystem.elements;

import net.gerritk.homesystem.interfaces.Service;

public interface ElementService<E extends Element> extends Service {
	public void registerElement(E element);

	public void unregisterElement(E element);

	public boolean isElementRegistered(E element);
}
