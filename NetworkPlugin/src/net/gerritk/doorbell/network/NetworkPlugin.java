package net.gerritk.doorbell.network;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;
import net.gerritk.doorbell.interfaces.DoorbellPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class NetworkPlugin implements DoorbellPlugin, Runnable {
	private Dispatcher dispatcher;
	private ServerSocket serverSocket;
	private boolean listen;
	private Thread listenerThread;

	@Override
	public boolean initialize() {
		System.out.println("[NetworkPlugin] Initializing...");
		boolean success = true;

		dispatcher = new Dispatcher();

		try {
			serverSocket = new ServerSocket(81);
			listen = true;
			listenerThread = new Thread(this);
			listenerThread.start();
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}

		if(success) {
			System.out.println("[NetworkPlugin] Finished initializing.");
		} else {
			System.err.println("[NetworkPlugin] Initializing failed.");
		}
		return success;
	}

	@Override
	public void dispose() {
		System.out.println("[NetworkPlugin] Disposing...");

		listen = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		listenerThread.interrupt();
		while (listenerThread.isAlive()) {
			Thread.yield();
		}

		System.out.println("[NetworkPlugin] Finished disposing.");
	}

	@Override
	public String toString() {
		return "NetworkPlugin";
	}

	@Override
	public void run() {
		while(listen) {
			try {
				Socket remote = serverSocket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
				PrintWriter out = new PrintWriter(remote.getOutputStream());

				JSONRPC2Request request = JSONRPC2Request.parse(in.readLine());
				JSONRPC2Response response = dispatcher.process(request, null);

				out.println(response);

				in.close();
				out.close();
				remote.close();
			} catch (SocketException e) {
				// Nothing
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONRPC2ParseException e) {
				e.printStackTrace();
			}
		}
	}
}
