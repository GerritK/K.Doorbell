package net.gerritk.doorbell.network.rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

public abstract class RpcModule implements RequestHandler {
	private final String namespace;
	private final String[] methods;

	public RpcModule(String namespace) {
		if(namespace == null || namespace.trim().isEmpty()) {
			throw new IllegalArgumentException("namespace must not be null or empty!");
		}

		Vector<String> methods = new Vector<String>();
		this.namespace = namespace;
		for(Method method : this.getClass().getMethods()) {
			if(method.isAnnotationPresent(RpcMethod.class)) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if((parameterTypes.length == 2 && parameterTypes[0] == JSONRPC2Request.class && parameterTypes[1] == MessageContext.class)
						|| (parameterTypes.length == 1 && parameterTypes[0] == JSONRPC2Request.class)) {
					methods.add(method.getName());
				} else {
					System.err.println("[ERROR] Invalid method for processing. the method have to take at least JSONRPC2Request and if needed MessageContext as paramters.");
				}
			}
		}
		this.methods = methods.toArray(new String[methods.size()]);
	}

	public final String getNamespace() {
		return namespace;
	}

	@Override
	public final String[] handledRequests() {
		Vector<String> fullMethods = new Vector<String>();
		for (String method : methods) {
			fullMethods.add(getNamespace() + "." + method);
		}
		return fullMethods.toArray(new String[fullMethods.size()]);
	}

	@Override
	public final JSONRPC2Response process(JSONRPC2Request request, MessageContext requestCtx) {
		String methodName = request.getMethod().replace(getNamespace() + ".", "");
		try {
			Method method = null;
			
			try {
				method = this.getClass().getMethod(methodName, JSONRPC2Request.class, MessageContext.class);
			} catch (NoSuchMethodException ignore) { }
			if(method != null) {
				Object result = method.invoke(this, request, requestCtx);
				return new JSONRPC2Response(result, request.getID());
			}

			try {
				method = this.getClass().getMethod(methodName, JSONRPC2Request.class);
			} catch (NoSuchMethodException ignore) {	}
			if(method != null) {
				Object result = method.invoke(this, request);
				return new JSONRPC2Response(result, request.getID());
			}
		} catch (InvocationTargetException e) {
			return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR);
		} catch (IllegalAccessException e) {
			return new JSONRPC2Response(JSONRPC2Error.INTERNAL_ERROR);
		}

		return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, request.getID());
	}
}
