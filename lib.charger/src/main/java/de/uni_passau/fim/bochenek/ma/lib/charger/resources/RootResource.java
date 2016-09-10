package de.uni_passau.fim.bochenek.ma.lib.charger.resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RootResource extends CoapResource {

	public RootResource() {
		super("");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN);

	}

}
