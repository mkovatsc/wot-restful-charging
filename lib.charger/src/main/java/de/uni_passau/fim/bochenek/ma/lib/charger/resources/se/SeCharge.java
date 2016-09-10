package de.uni_passau.fim.bochenek.ma.lib.charger.resources.se;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

public class SeCharge extends CoapResource {

	public SeCharge(String name, ChargerData data) {
		super(name);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN);
	}

}
