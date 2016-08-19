package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.JsonObject;

import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

public class EvChargingProcessPV extends CoapResource {

	private ChargerData chargerData;

	public EvChargingProcessPV(String name, ChargerData chargerData) {
		super(name);
		this.setObservable(true);
		this.chargerData = chargerData;
		this.chargerData.subscribe(this, "presentVoltage");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		JsonObject pV = new JsonObject();
		pV.addProperty("presentVoltage", chargerData.getPresentVoltage());
		exchange.respond(ResponseCode.CONTENT, pV.toString(), MediaTypeRegistry.APPLICATION_JSON);
	}

}
