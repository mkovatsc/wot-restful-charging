package de.uni_passau.fim.bochenek.ma.lib.charger;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.ServerMessageDeliverer;
import org.eclipse.californium.core.server.resources.Resource;

import de.uni_passau.fim.bochenek.ma.lib.charger.handler.HandlerType;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.IHandler;

public class CustomDeliverer extends ServerMessageDeliverer {

	private Map<HandlerType, IHandler> handlers;

	public CustomDeliverer(Resource root) {
		super(root);
		handlers = new HashMap<HandlerType, IHandler>();
	}

	@Override
	public void deliverRequest(Exchange exchange) {
		if (exchange.getRequest().getPayloadString() != null && !exchange.getRequest().getPayloadString().equals("")
				&& handlers.containsKey(HandlerType.DEFAULT)) {
			handlers.get(HandlerType.DEFAULT).callback(exchange.getRequest().getPayloadString());
		}
		super.deliverRequest(exchange);
	}

	/**
	 * 
	 * @param name
	 * @param handler
	 */
	protected void registerHandler(HandlerType type, IHandler handler) {
		handlers.put(type, handler);
	}

}
