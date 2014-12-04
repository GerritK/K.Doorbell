package net.gerritk.doorbell.network.servlets;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.util.ArrayList;

public class EventWebSocket extends WebSocketAdapter {
	private ArrayList<Session> sessions;

	@Override
	public void onWebSocketConnect(Session session) {
		System.out.println("SOCKET_CONNECT::" + session);
		super.onWebSocketConnect(session);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		cause.printStackTrace(System.err);
		super.onWebSocketError(cause);
	}

	@Override
	public void onWebSocketText(String message) {
		System.out.println("SOCKET_MESSAGE::" + message);
		super.onWebSocketText(message);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		System.out.println("SOCKET_CLOSED::[" + statusCode + "] " + reason);
		super.onWebSocketClose(statusCode, reason);
	}
}
