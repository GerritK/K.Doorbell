package net.gerritk.doorbell.client.notificator;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
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
	//public static String host = "localhost";
	public static String host = "192.168.178.39";

	private WebSocketClient webSocket;
	private JSONRPC2Session rpcSession;

	public Notificator() throws MalformedURLException {
		webSocket = new WebSocketClient();

		rpcSession = new JSONRPC2Session(new URL("http://" + host + ":81/jsonrpc"));

		Vector<Object> params = new Vector<Object>();
		params.add("hello world :)");
		try {
			String result = rpcSession.send(new JSONRPC2Request("test.echo", params, "0")).getResult().toString();
			System.out.println("ECHO::" + result);
		} catch (JSONRPC2SessionException e) {
			if(e.getCause() instanceof ConnectException) {
				System.err.println("Can not connect to '" + rpcSession.getURL() + "'!");
			} else {
				e.printStackTrace();
			}
		}

		try {
			URI uri = new URI("ws://" + host + ":81/websockets/");
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
		JSONObject object = (JSONObject) JSONValue.parse(msg);
		if(object.get("error") != null) {
			System.err.println(object.get("error"));
		} else if(object.get("result") != null) {
			JSONObject result = (JSONObject) object.get("result");
			String event = String.valueOf(result.get("event"));

			if(event != null && !event.trim().isEmpty()) {
				if(event.equals("doorbell.ring")) {
					String doorbell = String.valueOf(result.get("identifier"));
					if(doorbell != null) {
						Vector<Object> args = new Vector<Object>();
						args.add(doorbell);
						try {
							JSONRPC2Response response = rpcSession.send(new JSONRPC2Request("test.blink", args, "1"));
							System.out.println("RPC BLINK::" + response.getResult());
						} catch (JSONRPC2SessionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) throws MalformedURLException {
		Notificator notificator = new Notificator();
	}
}
