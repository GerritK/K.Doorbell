package net.gerritk.doorbell.network.servlets;

import net.gerritk.doorbell.events.DoorbellEvent;
import net.gerritk.doorbell.events.DoorbellListener;
import net.gerritk.doorbell.services.DoorbellService;
import net.gerritk.doorbell.services.ServiceContainer;
import net.minidev.json.JSONObject;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;

public class EventWebSocket extends WebSocketAdapter implements DoorbellListener {
	@Override
	public void onWebSocketConnect(Session session) {
		System.out.println("SOCKET_CONNECT::" + session);
		super.onWebSocketConnect(session);

		DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);
		doorbellService.registerListener(this);
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

		DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);
		doorbellService.unregisterListener(this);
	}

	@Override
	public void onRinging(DoorbellEvent event) {
		JSONObject result = new JSONObject();
		result.put("event", "doorbell.ringing");
		result.put("identifier", event.identifier);
		result.put("timestamp", event.timestamp);

		JSONObject send = new JSONObject();
		send.put("result", result);

		if(isConnected()) {
			try {
				RemoteEndpoint remote = getRemote();
				remote.sendString(send.toJSONString());
				remote.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
