package net.gerritk.doorbell.client.notificator;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class Notificator {
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
	}
}
