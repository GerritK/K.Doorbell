package net.gerritk.doorbell.events;

import net.gerritk.doorbell.annotations.SerializeToJSON;

public class DoorbellEvent extends EventObject {
	@SerializeToJSON
	public final String event;
	@SerializeToJSON
	public final String identifier;
	@SerializeToJSON
	public final long timestamp;

	public DoorbellEvent(String event, String identifier, long timestamp) {
		this.event = event;
		this.identifier = identifier;
		this.timestamp = timestamp;
	}
}
