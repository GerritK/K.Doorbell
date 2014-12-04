package net.gerritk.doorbell.client.notificator;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.*;
import java.util.Vector;

@WebSocket
public class Notificator {
	WebSocketClient webSocket;

	public Notificator() {
		webSocket = new WebSocketClient();
		try {
			URI uri = new URI("ws://localhost:81/websockets/");
			webSocket.start();
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			webSocket.connect(this, uri, request);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.println("CONNECT::" + session);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.println("CLOSE::" + statusCode + "," + reason);
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		System.out.println("MESSAGE::" + msg);
	}

	public static void main(String[] args) throws MalformedURLException {
		JSONRPC2Session session = new JSONRPC2Session(new URL("http://localhost:81/jsonrpc"));

		Vector<Object> params = new Vector<Object>();
		params.add("hello world :)");
		try {
			String result = session.send(new JSONRPC2Request("test.echo", params, "0")).getResult().toString();
			System.out.println("ECHO::" + result);
		} catch (JSONRPC2SessionException e) {
			if(e.getCause() instanceof ConnectException) {
				System.err.println("Can not connect to '" + session.getURL() + "'!");
			} else {
				e.printStackTrace();
			}
		}

		Notificator notificator = new Notificator();
	}
}
