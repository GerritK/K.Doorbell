package net.gerritk.homesystem.network.servlets;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Parser;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;
import net.gerritk.homesystem.network.rpc.DoorbellModule;
import net.gerritk.homesystem.network.rpc.TestModule;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonRpcServlet extends HttpServlet {
	private final Dispatcher dispatcher;
	private final JSONRPC2Parser parser;

	public JsonRpcServlet() {
		dispatcher = new Dispatcher();

		dispatcher.register(new TestModule());
		dispatcher.register(new DoorbellModule());

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
