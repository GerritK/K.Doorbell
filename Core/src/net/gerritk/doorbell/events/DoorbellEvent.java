package net.gerritk.doorbell.events;

import net.gerritk.doorbell.annotations.ConvertToJSON;

public class DoorbellEvent extends EventObject {
	@ConvertToJSON
	public final String event;
	@ConvertToJSON
	public final String identifier;
	@ConvertToJSON
	public final long timestamp;

	public DoorbellEvent(String event, String identifier, long timestamp) {
		this.event = event;
		this.identifier = identifier;
		this.timestamp = timestamp;
	}
}
