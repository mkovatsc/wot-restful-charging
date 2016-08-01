package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import black.door.hate.HalRepresentation;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;
import black.door.hate.HalResource;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;

public class EvChargingProcessTC extends CoapResource implements HalResource {

	private CarData carData;

	public EvChargingProcessTC(String name, CarData carData) {
		super(name);
		this.carData = carData;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		super.handleGET(exchange);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		super.handlePOST(exchange);
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

		return hal;
	}

}
