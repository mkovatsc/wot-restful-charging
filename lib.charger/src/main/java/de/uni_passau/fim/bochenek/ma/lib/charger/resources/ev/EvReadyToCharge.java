package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SePresentValues;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;

public class EvReadyToCharge extends CoapResource implements HalResource {

	private boolean readyToCharge;

	public EvReadyToCharge(String name) {
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

		// TODO not very robust :P
		Gson gson = new GsonBuilder().create();
		JsonObject tmp = gson.fromJson(exchange.getRequestText(), JsonObject.class);
		this.readyToCharge = tmp.get("readyToCharge").getAsBoolean();

		// TODO Ugly hack to try something out, there need to be a central entity holding the data
		if (this.readyToCharge) {
			Resource root = this.getParent().getParent().getParent();
			SePresentValues presentValues = (SePresentValues) root.getChild("se").getChild("presentValues");
			presentValues.setVoltage(400);
			presentValues.setCurrent(0);
		}

		exchange.respond(ResponseCode.CHANGED);
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

	public boolean isReadyToCharge() {
		return readyToCharge;
	}

}
