package net.gerritk.homesystem.exceptions;

public class InvalidDescriptionException extends Exception {
	public InvalidDescriptionException(String message) {
		super(message);
	}

	public InvalidDescriptionException() {
		super("invalid plugin.json");
	}

	public InvalidDescriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDescriptionException(Throwable cause) {
		super("invalid plugin.json", cause);
	}
}
