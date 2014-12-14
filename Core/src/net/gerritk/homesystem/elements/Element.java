package net.gerritk.homesystem.elements;

public interface Element<E> {
	public void setService(ElementService<Doorbell> elementService);

	public ElementService<? extends E> getService();
}
