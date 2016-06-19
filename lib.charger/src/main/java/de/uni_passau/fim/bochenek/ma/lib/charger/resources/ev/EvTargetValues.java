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
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SePresentValues;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;

public class EvTargetValues extends CoapResource implements HalResource {

	private double voltage;
	private double current;

	public EvTargetValues(String name) {
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
		JsonObject targetVals = gson.fromJson(exchange.getRequestText(), JsonObject.class);
		this.voltage = targetVals.get("targetVoltage").getAsDouble();
		this.current = targetVals.get("targetCurrent").getAsDouble();

		// TODO Just for testing purposes, charger sets the requested values instantly
		SePresentValues tmp = (SePresentValues) this.getParent().getParent().getParent().getChild("se").getChild("presentValues");
		tmp.setVoltage(this.voltage);
		tmp.setCurrent(this.current);
		tmp.changed();

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
		hal.addProperty("voltage", this.voltage);
		hal.addProperty("current", this.current);

		return hal;
	}

	public double getVoltage() {
		return voltage;
	}

	public double getCurrent() {
		return current;
	}

}
