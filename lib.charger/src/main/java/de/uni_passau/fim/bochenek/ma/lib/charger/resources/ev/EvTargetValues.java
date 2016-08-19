package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SePresentValues;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import ch.ethz.inf.vs.hypermedia.corehal.model.Link;

public class EvTargetValues extends CoapResource {

	private CarData data;

	public EvTargetValues(String name, CarData data) {
		super(name);
		this.data = data;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, this.getRepresentation().toString(), MediaTypeRegistry.APPLICATION_JSON);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {

		// TODO not very robust :P
		Gson gson = new GsonBuilder().create();
		JsonObject targetVals = gson.fromJson(exchange.getRequestText(), JsonObject.class);
		this.data.setTargetVoltage(targetVals.get("targetVoltage").getAsDouble());
		this.data.setTargetCurrent(targetVals.get("targetCurrent").getAsDouble());

		// TODO Just for testing purposes, charger sets the requested values instantly
		SePresentValues tmp = (SePresentValues) this.getParent().getParent().getParent().getChild("se").getChild("presentValues");
		tmp.setVoltage(this.data.getTargetVoltage());
		tmp.setCurrent(this.data.getTargetCurrent());
		tmp.changed();

		exchange.respond(ResponseCode.CHANGED);
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	private CoREHalBase getRepresentation() {
		CoREHalBase hal = new CoREHalBase();
		hal.addLink("self", new Link(this.getURI()));

		// TODO embedded resources?

		return hal;
	}

}
