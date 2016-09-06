package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import ch.ethz.inf.vs.hypermedia.client.Utils;
import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import ch.ethz.inf.vs.hypermedia.corehal.model.Form;
import ch.ethz.inf.vs.hypermedia.corehal.model.Link;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.EventMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;
import de.uni_passau.fim.bochenek.ma.util.server.forms.ChargeForm;

public class EvChargingTask extends CoapResource {

	private ChargerData chargerData;
	private CarData carData;

	public EvChargingTask(String name, ChargerData chargerData, CarData carData) {
		super(name);
		this.chargerData = chargerData;
		this.carData = carData;

		EvChargingTaskPV evChargingTaskPV = new EvChargingTaskPV("pV", this.chargerData);
		evChargingTaskPV.setVisible(false);
		EvChargingTaskPC evChargingTaskPC = new EvChargingTaskPC("pC", this.chargerData);
		evChargingTaskPC.setVisible(false);
		this.add(evChargingTaskPV);
		this.add(evChargingTaskPC);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, this.getRepresentation().toString(), MediaTypeRegistry.APPLICATION_JSON);
	}

	@Override
	public void handlePUT(CoapExchange exchange) {
		// TODO not allowed, i.e. if voltages don't match yet

		Gson gson = new GsonBuilder().create();
		JsonObject formData = gson.fromJson(exchange.getRequestText(), JsonObject.class); // TODO not very robust...
		ChargeForm charge = new ChargeForm(formData);
		carData.setSoc(charge.getSoc());
		carData.setTargetCurrent(charge.getTargetCurrent());

		EventMessage eMsg = new EventMessage(null);
		eMsg.setTargetCurrent(carData.getTargetCurrent());
		eMsg.setDescription("targetCurrentSet");
		SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg);

		exchange.respond(ResponseCode.CHANGED, "", MediaTypeRegistry.APPLICATION_JSON); // TODO content?
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		carData.setTargetVoltage(0);
		carData.setTargetCurrent(0); // TODO Also tell ChargerUI to cut voltage supply

		// Tell charger that the target voltage was updated
		EventMessage eMsg = new EventMessage(null);
		eMsg.setTargetCurrent(carData.getTargetVoltage());
		eMsg.setDescription("targetVoltageSet");
		SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg);
		
		exchange.setLocationPath(carData.getBookmarks().get("evLoc").getURI());
		this.delete();
		exchange.respond(ResponseCode.DELETED);
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	private CoREHalBase getRepresentation() {
		CoREHalBase hal = new CoREHalBase();
		hal.addLink("self", new Link(this.getURI()));

		for (Resource child : this.getChildren()) {
			hal.addLink(child.getName(), new Link(child.getURI()));
		}

		System.out.println(chargerData.getCableCheckStatus() + " - " + chargerData.getPresentVoltage() + " - " + carData.getTargetVoltage());
		if (chargerData.getCableCheckStatus() == 2 && chargerData.getPresentVoltage() == carData.getTargetVoltage()) { // TODO define acceptance range for voltage
			Form charge = new Form("PUT", this.getURI(), Utils.getMediaType(ChargeForm.class));
			hal.addForm("charge", charge);
		}

		// TODO Check for present current first!
		Form stop = new Form("DELETE", this.getURI(), ""); // TODO define accepts
		hal.addForm("stop", stop);

		return hal;
	}

}
