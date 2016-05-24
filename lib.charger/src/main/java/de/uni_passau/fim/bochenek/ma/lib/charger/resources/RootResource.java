package de.uni_passau.fim.bochenek.ma.lib.charger.resources;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;

import black.door.hate.HalRepresentation;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;
import black.door.hate.HalResource;

public class RootResource extends CoapResource implements HalResource {

	public RootResource() {
		super("");
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
			return new URI("/");
		} catch (URISyntaxException e) {
			return null;
		}
	}

	@Override
	public HalRepresentationBuilder representationBuilder() {
		HalRepresentationBuilder hal = HalRepresentation.builder();
		hal.addLink("self", this);

		for (Resource res : this.getChildren()) {
			if (!res.getName().equals(".well-known")) {
				hal.addLink(res.getName(), (HalResource) res);
			}
		}

		return hal;
	}

}
