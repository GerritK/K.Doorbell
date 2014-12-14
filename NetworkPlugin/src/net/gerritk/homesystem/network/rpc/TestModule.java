package net.gerritk.homesystem.network.rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

import java.util.List;

public class TestModule extends RpcModule {
	public TestModule() {
		super("test");
	}

	@RpcMethod
	public Object echo(JSONRPC2Request request) {
		List<Object> params = request.getPositionalParams();
		return params != null ? params.get(0) : null;
	}
}
