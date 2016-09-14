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
		if (chargerData.getPresentVoltage() == carData.getTargetVoltage()) { // TODO Define acceptance range?
			Gson gson = new GsonBuilder().create();
			JsonObject formData = gson.fromJson(exchange.getRequestText(), JsonObject.class); // TODO not very robust...
			ChargeForm charge = new ChargeForm(formData);
			carData.setSoc(charge.getSoc());
			carData.setTargetCurrent(charge.getTargetCurrent());
			carData.setCharging(true);

			EventMessage eMsg = new EventMessage(null);
			eMsg.setTargetCurrent(carData.getTargetCurrent());
			eMsg.setDescription("targetCurrentSet");
			SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg);
			
			// TODO As the UI might need a second to update the present current, the links shown to the car are invalid!
			// TODO Maybe make use of a "dirty" flag, to rather show a incomplete representation instead of an invalid one.

			exchange.respond(ResponseCode.CHANGED, "", MediaTypeRegistry.APPLICATION_JSON);
		} else {
			exchange.respond(ResponseCode.PRECONDITION_FAILED); // TODO Correct response code?
		}
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		carData.setTargetVoltage(0);
		carData.setTargetCurrent(0);
		carData.setCharging(false);

		// Tell charger that the target voltage and current was updated
		EventMessage eMsg = new EventMessage(null);
		eMsg.setTargetVoltage(carData.getTargetVoltage());
		eMsg.setTargetCurrent(carData.getTargetCurrent());
		eMsg.setDescription("targetVoltageSet");
		SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg);
		eMsg.setDescription("targetCurrentSet");
		SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg);

		exchange.setLocationPath(carData.getBookmarks().get("evLoc").getURI());
		this.getChildren().forEach(child -> ((CoapResource) child).clearAndNotifyObserveRelations(ResponseCode.NOT_FOUND)); // TODO evil cast!
		this.delete();
		exchange.respond(ResponseCode.DELETED);
	}

	private CoREHalBase getRepresentation() {
		CoREHalBase hal = new CoREHalBase();

		Link self = new Link(this.getURI());
		if (chargerData.getCableCheckStatus() != 2 || chargerData.getPresentVoltage() != carData.getTargetVoltage()) { // TODO Refactor those conditional attributes
			hal.addLink("wait", self); // TODO Not always correct, is there a process running?
		} else {
			hal.addLink("self", self);
		}

		// TODO Applies to all links: Set the types?
		for (Resource child : this.getChildren()) {
			hal.addLink(child.getName(), new Link(child.getURI()));
		}

		if (chargerData.getCableCheckStatus() == 2 && chargerData.getPresentVoltage() == carData.getTargetVoltage()) { // TODO define acceptance range for voltage
			Form charge = new Form("PUT", this.getURI(), Utils.getMediaType(ChargeForm.class));
			charge.setNames("charge");
			hal.addForm("continue", charge);
		}

		// Only provide the form if current is ramped down
		if (chargerData.getPresentCurrent() == 0) {
			Form stop = new Form("DELETE", this.getURI(), ""); // TODO define accepts
			hal.addForm("stop", stop);
		}

		return hal;
	}

}
