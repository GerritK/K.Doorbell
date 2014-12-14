package net.gerritk.homesystem.network.rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import net.gerritk.homesystem.Doorbell;
import net.gerritk.homesystem.services.DoorbellService;
import net.gerritk.homesystem.services.ServiceContainer;

import java.util.List;

public class DoorbellModule extends RpcModule {
	public DoorbellModule() {
		super("homesystem");
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
