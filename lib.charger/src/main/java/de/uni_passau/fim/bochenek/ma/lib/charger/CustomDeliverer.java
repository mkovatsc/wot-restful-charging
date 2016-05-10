package de.uni_passau.fim.bochenek.ma.lib.charger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.ServerMessageDeliverer;
import org.eclipse.californium.core.server.resources.Resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.uni_passau.fim.bochenek.ma.lib.charger.handler.IHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;

public class CustomDeliverer extends ServerMessageDeliverer {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Map<MessageType, IHandler> handlers;

	public CustomDeliverer(Resource root) {
		super(root);
		handlers = new HashMap<MessageType, IHandler>();
	}

	@Override
	public void deliverRequest(Exchange exchange) {

		// TODO Error when blockwise transfer?
		String payload = exchange.getRequest().getPayloadString();

		if (payload != null && !payload.equals("")) {
			JsonParser parser = new JsonParser();
			JsonElement msg = null;
			try {
				msg = parser.parse(payload);
			} catch (JsonSyntaxException jse) {
				// TODO Something to do here?
				logger.log(Level.WARNING, "No valid JSON received. (" + payload + ")");
			}

			if (msg != null && msg.isJsonObject() && msg.getAsJsonObject().get("type") != null) {
				try {
					MessageType type = MessageType.valueOf(msg.getAsJsonObject().get("type").getAsString());
					if (handlers.containsKey(type)) {
						handlers.get(type).callback(payload);
					} else {
						logger.log(Level.INFO, "No handler found for this type of message. (" + payload + ")");
					}
				} catch (IllegalArgumentException iae) {
					// TODO Find some elegant solution
					logger.log(Level.WARNING, "No handler found for this type of message. (" + payload + ")");
				}
			}
		}

		super.deliverRequest(exchange);
	}

	/**
	 * 
	 * @param name
	 * @param handler
	 */
	protected void registerHandler(MessageType type, IHandler handler) {
		handlers.put(type, handler);
	}

}
