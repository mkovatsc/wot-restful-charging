package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.JsonObject;

import black.door.hate.HalRepresentation;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;
import black.door.hate.HalResource;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

public class EvChargingProcessPC extends CoapResource implements HalResource {

	private ChargerData chargerData;

	public EvChargingProcessPC(String name, ChargerData chargerData) {
		super(name);
		this.setObservable(true);
		this.chargerData = chargerData;
		this.chargerData.subscribe(this, "presentCurrent");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		JsonObject pC = new JsonObject();
		pC.addProperty("presentCurrent", chargerData.getPresentCurrent());
		exchange.respond(ResponseCode.CONTENT, pC.toString(), MediaTypeRegistry.APPLICATION_JSON);
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