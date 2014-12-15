package net.gerritk.homesystem.elements;

public interface Element<E extends Element> {
	public void setService(ElementService<E> elementService);

	public ElementService<E> getService();
}
