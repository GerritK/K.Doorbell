package net.gerritk.homesystem.client.notificator;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.frames.PongFrame;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

@WebSocket
public class Notificator {
	private WebSocketClient webSocket;
	private Session session;
	private JSONRPC2Session rpcSession;
	private URI webSocketUri;
	private long ping;

	public Notificator(String host) throws MalformedURLException {
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
			webSocketUri = new URI("ws://" + host + ":81/websockets/");
			webSocket.start();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		connect();
	}

	public void connect() {
		ClientUpgradeRequest request = new ClientUpgradeRequest();
		try {
			webSocket.connect(this, webSocketUri, request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.println("CONNECT::" + session.getRemoteAddress());
		this.session = session;

		final Timer timer = new Timer();
		final Session finalSession = session;
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if(finalSession.isOpen()) {
					RemoteEndpoint remote = finalSession.getRemote();
					byte[] bytes = String.valueOf(System.currentTimeMillis()).getBytes(Charset.forName("UTF-8"));
					ByteBuffer payload = ByteBuffer.wrap(bytes);
					try {
						remote.sendPing(payload);
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("PING@" + finalSession.getRemoteAddress());
				} else {
					timer.cancel();
				}
			}
		};
		timer.scheduleAtFixedRate(timerTask, 10 * 1000, 60 * 1000);
	}

	@OnWebSocketFrame
	public void onFrame(Frame frame) {
		if(frame instanceof PongFrame) {
			PongFrame pongFrame = (PongFrame) frame;
			String payload = pongFrame.getPayloadAsUTF8();
			try {
				long lastPing = Long.parseLong(payload);
				ping = System.currentTimeMillis() - lastPing;
				System.out.println("PING::" + ping);
			} catch (NumberFormatException e) {
				System.out.println("PING::Invalid response: " + payload);
			}
		}
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.println("CLOSE::" + statusCode + "," + reason);
		session = null;
		connect();
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
						args.add(1000);
						try {
							JSONRPC2Response response = rpcSession.send(new JSONRPC2Request("doorbell.setOutput", args, "1"));
							if(response.indicatesSuccess()) {
								System.out.println("RESPONSE::" + response.getResult());
							} else {
								System.err.println("ERROR::" + response.getError());
							}
						} catch (JSONRPC2SessionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) throws MalformedURLException {
		if(args.length >= 1) {
			Notificator notificator = new Notificator(args[0]);
		}
	}
}
