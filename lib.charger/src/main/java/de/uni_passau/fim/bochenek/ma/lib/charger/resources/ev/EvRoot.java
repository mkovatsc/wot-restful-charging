package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;

import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import black.door.hate.LinkOrResource;
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
		UUID uuid = UUID.randomUUID(); // TODO has to be done in the emulator!

		Map<String, CoapResource> resources = new HashMap<String, CoapResource>();
		resources.put("chargingComplete", new EvChargingComplete("chargingComplete"));
		resources.put("maxValues", new EvMaxValues("maxValues"));
		resources.put("readyToCharge", new EvReadyToCharge("readyToCharge"));
		resources.put("stateOfCharge", new EvSoc("stateOfCharge"));
		resources.put("targetValues", new EvTargetValues("targetValues"));

		String actionResult = null;
		HalRepresentationBuilder result = HalRepresentation.builder();
		EvID ev = new EvID(uuid.toString());

		for (Map.Entry<String, CoapResource> res : resources.entrySet()) {
			ev.add(res.getValue());
			result.addLink(res.getKey(), (LinkOrResource) res.getValue());
		}

		this.add(ev);
		try {
			actionResult = result.build().serialize();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		exchange.setLocationPath("/ev/" + uuid.toString()); // TODO better way?
		exchange.respond(ResponseCode.CREATED, actionResult, MediaTypeRegistry.APPLICATION_JSON);
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
