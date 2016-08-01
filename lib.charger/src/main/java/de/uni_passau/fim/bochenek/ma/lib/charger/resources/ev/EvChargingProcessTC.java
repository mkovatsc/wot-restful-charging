package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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
	public void handlePUT(CoapExchange exchange) {
		JsonParser parser = new JsonParser(); // TODO use Gson
		JsonElement msg = null;

		try {
			msg = parser.parse(exchange.getRequestText());
		} catch (JsonSyntaxException jse) {
			// TODO Something to do here?
		}

		if (msg != null && msg.isJsonObject() && msg.getAsJsonObject().get("targetCurrent") != null) {
			carData.setTargetCurrent(msg.getAsJsonObject().get("targetCurrent").getAsDouble());
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

		return hal;
	}

}
