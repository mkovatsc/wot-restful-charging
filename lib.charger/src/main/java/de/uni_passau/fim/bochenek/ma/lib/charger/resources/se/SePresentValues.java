package de.uni_passau.fim.bochenek.ma.lib.charger.resources.se;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import ch.ethz.inf.vs.hypermedia.corehal.model.Link;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

public class SePresentValues extends CoapResource {

	private ChargerData data;

	public SePresentValues(String name, ChargerData data) {
		super(name);
		this.setObservable(true); // TODO
		this.data = data;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, this.getRepresentation().toString(), MediaTypeRegistry.APPLICATION_JSON);
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

	public void setVoltage(double voltage) {
		this.data.setPresentVoltage(voltage);
	}

	public void setCurrent(double current) {
		this.data.setPresentCurrent(current);
	}

}
