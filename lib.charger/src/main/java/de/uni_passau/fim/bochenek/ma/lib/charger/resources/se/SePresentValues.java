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
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

public class SePresentValues extends CoapResource implements HalResource {

	private ChargerData data;

	public SePresentValues(String name, ChargerData data) {
		super(name);
		this.setObservable(true); // TODO
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
		hal.addProperty("voltage", this.data.getPresentVoltage());
		hal.addProperty("current", this.data.getPresentCurrent());

		return hal;
	}

	public void setVoltage(double voltage) {
		this.data.setPresentVoltage(voltage);
	}

	public void setCurrent(double current) {
		this.data.setPresentCurrent(current);
	}

}
