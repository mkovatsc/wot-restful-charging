package de.uni_passau.fim.bochenek.ma.lib.charger.resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class TestResource extends CoapResource {

	public TestResource(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, "Hello, World!", MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		exchange.respond(ResponseCode.VALID);
	}

}