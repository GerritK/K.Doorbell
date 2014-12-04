package net.gerritk.doorbell.events;

public class DoorbellEvent {
	public final String identifier;
	public final long timestamp;

	public DoorbellEvent(String identifier, long timestamp) {
		this.identifier = identifier;
		this.timestamp = timestamp;
	}
}
