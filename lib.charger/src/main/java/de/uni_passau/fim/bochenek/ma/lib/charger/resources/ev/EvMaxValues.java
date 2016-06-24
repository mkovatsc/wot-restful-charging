package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;

public class EvMaxValues extends CoapResource implements HalResource {

	private CarData data;

	public EvMaxValues(String name, CarData data) {
		super(name);
		this.data = data;
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
		JsonObject maxVals = gson.fromJson(exchange.getRequestText(), JsonObject.class);
		this.data.setMaxVoltage(maxVals.get("maxVoltage").getAsDouble());
		this.data.setMaxCurrent(maxVals.get("maxCurrent").getAsDouble());

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
		hal.addProperty("voltage", this.data.getMaxVoltage());
		hal.addProperty("current", this.data.getMaxCurrent());

		return hal;
	}

}
