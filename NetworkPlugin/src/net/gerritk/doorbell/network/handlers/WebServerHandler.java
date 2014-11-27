package net.gerritk.doorbell.network.handlers;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Parser;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebServerHandler extends AbstractHandler {
	private HashMap<String, Handler> handlers;

	public WebServerHandler() {
		handlers = new HashMap<String, Handler>();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		JSONRPC2Parser parser = new JSONRPC2Parser();
		while (target.endsWith("/") && target.length() > 1) {
			target = target.substring(0, target.length() - 1);
		}

		for(Map.Entry<String, Handler> entry : handlers.entrySet()) {
			if(entry.getKey().equals(target) && !baseRequest.isHandled()) {
				entry.getValue().handle(target, baseRequest, request, response);
			}
		}
	}

	public boolean addHandler(String target, Handler handler) {
		if(target != null && !target.trim().isEmpty() && handler != null) {
			if(!handlers.containsKey(target)) {
				handlers.put(target, handler);
			} else {
				System.err.println("[NetworkPlugin] Handler for target '" + target + "' already defined!");
				return false;
			}
		} else if(handler == null) {
			System.err.println("[NetworkPlugin] Handler must not be null!");
			return false;
		} else {
			System.err.println("[NetworkPlugin] Target must not be null or empty!");
			return false;
		}
		return true;
	}

	public boolean removeHandler(String target) {
		return handlers.remove(target) != null;
	}

	public boolean removeHandler(Handler handler) {
		boolean result = true;
		for(Map.Entry<String, Handler> entry : handlers.entrySet()) {
			if(entry.getValue() == handler) {
				result = result && handlers.remove(entry.getKey()) != null;
			}
		}
		return result;
	}
}
