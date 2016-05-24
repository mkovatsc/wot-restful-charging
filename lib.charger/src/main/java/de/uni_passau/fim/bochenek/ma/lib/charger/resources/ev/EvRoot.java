package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;

import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;

public class EvRoot extends CoapResource implements HalResource {

	public EvRoot(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		try {
			exchange.respond(ResponseCode.CONTENT, this.asEmbedded().serialize(), MediaTypeRegistry.APPLICATION_JSON);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	@Override
	public URI location() {
		try {
			return new URI(this.getURI());
		} catch (URISyntaxException e) {
			return null;
		}
	}

	@Override
	public HalRepresentationBuilder representationBuilder() {
		HalRepresentationBuilder hal = HalRepresentation.builder();
		hal.addLink("self", this);

		for (Resource res : this.getChildren()) {
			hal.addLink(res.getName(), (HalResource) res);
		}

		return hal;
	}

}
