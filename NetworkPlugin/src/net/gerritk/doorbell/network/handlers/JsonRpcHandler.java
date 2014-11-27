package net.gerritk.doorbell.network.handlers;

import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class JsonRpcHandler extends AbstractHandler {
	private final Dispatcher dispatcher;
	private final JSONRPC2Parser parser;

	public JsonRpcHandler() {
		dispatcher = new Dispatcher();
		dispatcher.register(new RequestHandler() {
			@Override
			public String[] handledRequests() {
				return new String[] {
						"test.echo"
				};
			}

			@Override
			public JSONRPC2Response process(JSONRPC2Request request, MessageContext requestCtx) {
				if(request.getMethod().equals("test.echo")) {
					List params = (List) request.getPositionalParams();
					Object input = params.get(0);
					return new JSONRPC2Response(input, request.getID());
				} else {
					return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, request.getID());
				}
			}
		});
		parser = new JSONRPC2Parser();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (request.getMethod().equals("POST")) {
			BufferedReader reader = request.getReader();
			String line = reader.readLine();

			if (line != null && !line.trim().isEmpty()) {
				JSONRPC2Request jsonRequest = null;
				try {
					jsonRequest = parser.parseJSONRPC2Request(line);
				} catch (JSONRPC2ParseException e) {
					e.printStackTrace();
				}

				if (jsonRequest != null) {
					JSONRPC2Response jsonResponse = dispatcher.process(jsonRequest, null);

					response.setContentType("application/json-rpc");
					response.setStatus(HttpServletResponse.SC_OK);
					baseRequest.setHandled(true);
					response.getWriter().write(jsonResponse.toJSONString());
				}
			}
		}
	}
}
