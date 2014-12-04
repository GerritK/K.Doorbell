package net.gerritk.doorbell.events;

public interface DoorbellListener {
	public void onRinging(DoorbellEvent event);
}
