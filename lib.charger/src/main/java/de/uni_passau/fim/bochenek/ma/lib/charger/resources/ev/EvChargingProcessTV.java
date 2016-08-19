package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;

public class EvChargingProcessTV extends CoapResource {

	private CarData carData;

	public EvChargingProcessTV(String name, CarData carData) {
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

		if (msg != null && msg.isJsonObject() && msg.getAsJsonObject().get("targetVoltage") != null) {
			carData.setTargetVoltage(msg.getAsJsonObject().get("targetVoltage").getAsDouble());
			exchange.respond(ResponseCode.CHANGED);
		} else {
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}

}
