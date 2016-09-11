package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import de.uni_passau.fim.bochenek.ma.lib.charger.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.EventMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;
import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import ch.ethz.inf.vs.hypermedia.corehal.model.Form;
import ch.ethz.inf.vs.hypermedia.corehal.model.Link;

public class EvID extends CoapResource {

	private boolean chargingInit = false; // TODO Could be left out, equals evCharge == null
	private EvCharge evCharge;

	private ChargerData chargerData;
	private CarData carData;

	public EvID(String name, ChargerData chargerData, CarData carData) {
		super(name);
		this.chargerData = chargerData;
		this.carData = carData;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, this.getRepresentation().toString(), MediaTypeRegistry.APPLICATION_JSON);
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		if (chargerData.removeCar(carData.getUuid()) != null) { // TODO Maybe not needed, if resource exists but map entry not, there are other problems :P
			this.delete();

			// DEBUG
			SocketHandler socket = SocketHandler.getInstance();
			socket.pushToListeners(MessageType.DEBUG, new Message("Car (" + carData.getUuid().toString() + ") disconnected."));

			EventMessage eMsg = new EventMessage(null, false);
			eMsg.setDescription("unplugged");
			SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg); // TODO Provide UUID?

			exchange.setLocationPath("/.well-known/core"); // Reset
			exchange.respond(ResponseCode.DELETED);
		} else {
			exchange.respond(ResponseCode.NOT_FOUND); // TODO Correct response code?
		}
	}

	private CoREHalBase getRepresentation() {
		CoREHalBase hal = new CoREHalBase();

		Link self = new Link(this.getURI());
		if ((chargerData.getPresentVoltage() != 0 || chargerData.getTargetCurrent() != 0) || (chargerData.getCableCheckStatus() != 2 && !chargingInit)) {
			hal.addLink("wait", self);
		} else {
			hal.addLink("self", self);
		}

		// Add link to charging, if cable check was successfully completed
		if (chargerData.getCableCheckStatus() == 2 && !chargingInit) {
			evCharge = new EvCharge(this.getName(), chargerData, carData);
			evCharge.setVisible(false);
			this.getParent().getParent().getChild("charge").add(evCharge); // TODO This referencing is mad!
			chargingInit = true;
		}

		// Add link to charge resource
		if (chargingInit && evCharge != null) {
			hal.addLink("charge", new Link(evCharge.getURI()));
		}

		// Only provide the form if voltage and current are ramped down
		if (chargerData.getPresentVoltage() == 0 && chargerData.getTargetCurrent() == 0) {
			Form leave = new Form("DELETE", this.getURI(), ""); // TODO define accepts
			hal.addForm("leave", leave);
		}

		// TODO embedded resources?

		return hal;
	}

}
