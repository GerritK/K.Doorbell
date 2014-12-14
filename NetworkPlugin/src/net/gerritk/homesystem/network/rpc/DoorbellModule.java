package net.gerritk.homesystem.network.rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import net.gerritk.homesystem.elements.Doorbell;
import net.gerritk.homesystem.elements.DoorbellService;
import net.gerritk.homesystem.services.ServiceContainer;

import java.util.List;

public class DoorbellModule extends RpcModule {
	public DoorbellModule() {
		super("doorbell");
	}

	@RpcMethod
	public boolean setOutput(JSONRPC2Request request) {
		List params = (List) request.getPositionalParams();

		final DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);
		Doorbell doorbell = doorbellService.getDoorbell(params.get(0).toString());
		boolean enabled = false;
		long duration = -1;

		if(params.get(1) instanceof Boolean) {
			enabled = (Boolean) params.get(1);
		} else if(params.get(1) instanceof Number) {
			duration = (Long) params.get(1);
		}

		if(doorbell != null) {
			if(duration < 0) {
				doorbell.setOutput(enabled);
			} else {
				doorbell.setOutput(duration);
			}
			return true;
		}
		return false;
	}
}
