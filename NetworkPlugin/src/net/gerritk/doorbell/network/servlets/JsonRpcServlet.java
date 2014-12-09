package net.gerritk.doorbell.network.servlets;

import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;
import net.gerritk.doorbell.Doorbell;
import net.gerritk.doorbell.services.DoorbellService;
import net.gerritk.doorbell.services.ServiceContainer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class JsonRpcServlet extends HttpServlet {
	private final Dispatcher dispatcher;
	private final JSONRPC2Parser parser;

	public JsonRpcServlet() {
		dispatcher = new Dispatcher();
		dispatcher.register(new RequestHandler() {
			@Override
			public String[] handledRequests() {
				return new String[] {
						"test.echo",
						"test.blink"
				};
			}

			@Override
			public JSONRPC2Response process(JSONRPC2Request request, MessageContext requestCtx) {
				if(request.getMethod().equals("test.echo")) {
					List params = (List) request.getPositionalParams();
					Object input = params.get(0);
					return new JSONRPC2Response(input, request.getID());
				} else if(request.getMethod().equals("test.blink")) {
					List params = (List) request.getPositionalParams();

					final DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);
					Doorbell doorbell = doorbellService.getDoorbell(params.get(0).toString());
					if(doorbell != null) {
						doorbell.blink();
						return new JSONRPC2Response(true, request.getID());
					}
					return new JSONRPC2Response(false, request.getID());
				} else {
					return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, request.getID());
				}
			}
		});
		parser = new JSONRPC2Parser();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String contentType = req.getContentType();
		if(!contentType.equals("application/json-rpc") && !contentType.equals("application/json") && !contentType.equals("application/jsonrequest")) {
			resp.setContentType("application/json-rpc");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		BufferedReader reader = req.getReader();
		String line = reader.readLine();

		if (line != null && !line.trim().isEmpty()) {
			JSONRPC2Request jsonRequest = null;
			try {
				jsonRequest = parser.parseJSONRPC2Request(line);
			} catch (JSONRPC2ParseException e) {
				resp.setContentType("application/json-rpc");
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				e.printStackTrace();
			}

			if (jsonRequest != null) {
				JSONRPC2Response jsonResponse = dispatcher.process(jsonRequest, null);

				resp.setContentType("application/json-rpc");
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().write(jsonResponse.toJSONString());
			}
		}
	}
}
