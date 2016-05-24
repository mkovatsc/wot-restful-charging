package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.util.UUID;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class EvRoot extends CoapResource {

	public EvRoot(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		UUID uuid = UUID.randomUUID();
		EvID ev = new EvID(uuid.toString());
		ev.add(new EvChargingComplete("chargingComplete"));
		ev.add(new EvMaxValues("maxValues"));
		ev.add(new EvReadyToCharge("readyToCharge"));
		ev.add(new EvSoc("stateOfCharge"));
		ev.add(new EvTargetValues("targetValues"));
		this.add(ev);

		exchange.respond(ResponseCode.CREATED);
	}

}
