package net.gerritk.doorbell.network.rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import net.gerritk.doorbell.Doorbell;
import net.gerritk.doorbell.services.DoorbellService;
import net.gerritk.doorbell.services.ServiceContainer;

import java.util.List;

public class DoorbellModule extends RpcModule {
	public DoorbellModule() {
		super("doorbell");
	}

	@RpcMethod
	public boolean blink(JSONRPC2Request request) {
		List params = (List) request.getPositionalParams();

		final DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);
		Doorbell doorbell = doorbellService.getDoorbell(params.get(0).toString());

		if(doorbell != null) {
			doorbell.blink();
			return true;
		}
		return false;
	}
}
