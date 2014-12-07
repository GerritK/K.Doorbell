package net.gerritk.doorbell.network;

import net.gerritk.doorbell.plugins.Plugin;
import net.gerritk.doorbell.network.servlets.EventServlet;
import net.gerritk.doorbell.network.servlets.JsonRpcServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;

public class NetworkPlugin extends Plugin {
	private Server server;

	@Override
	public boolean initialize() {
		Log.setLog(new NoLogging());

		System.out.println("[NetworkPlugin] Initializing...");
		boolean success = true;

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		context.addServlet(new ServletHolder(new JsonRpcServlet()), "/jsonrpc/*");
		context.addServlet(new ServletHolder(new EventServlet()), "/websockets/*");
		// TODO add file servlet

		server = new Server(81);
		server.setStopAtShutdown(true);
		server.setHandler(context);

		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

		if (success) {
			System.out.println("[NetworkPlugin] Finished initializing.");
		} else {
			System.err.println("[NetworkPlugin] Initializing failed.");
		}
		return success;
	}

	@Override
	public void dispose() {
		System.out.println("[NetworkPlugin] Disposing...");

		if(server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("[NetworkPlugin] Finished disposing.");
	}

	@Override
	public String toString() {
		return "NetworkPlugin";
	}
}
