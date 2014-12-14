package net.gerritk.homesystem.exceptions;

public class InvalidPluginException extends Exception {
	public InvalidPluginException() {
		super();
	}

	public InvalidPluginException(String message) {
		super(message);
	}

	public InvalidPluginException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPluginException(Throwable cause) {
		super(cause);
	}
}
