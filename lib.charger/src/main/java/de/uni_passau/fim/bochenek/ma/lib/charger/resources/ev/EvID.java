package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class EvID extends CoapResource {

	public EvID(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		this.delete();
		exchange.respond(ResponseCode.DELETED);
	}

}
