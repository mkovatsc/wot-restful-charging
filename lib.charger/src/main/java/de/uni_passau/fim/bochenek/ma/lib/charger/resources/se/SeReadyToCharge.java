package de.uni_passau.fim.bochenek.ma.lib.charger.resources.se;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.fasterxml.jackson.core.JsonProcessingException;

import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;

public class SeReadyToCharge extends CoapResource implements HalResource {

	private boolean readyToCharge;

	public SeReadyToCharge(String name) {
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
		hal.addProperty("readyToCharge", readyToCharge);

		return hal;
	}

}
