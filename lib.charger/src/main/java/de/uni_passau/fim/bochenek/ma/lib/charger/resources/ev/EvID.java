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
		super(name); // TODO Do something with CarData?!
		this.chargerData = chargerData;
		this.carData = carData;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, this.getRepresentation().toString(), MediaTypeRegistry.APPLICATION_JSON);
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		chargerData.removeCar(carData.getUuid());

		this.delete();

		// DEBUG
		SocketHandler socket = SocketHandler.getInstance();
		socket.pushToListeners(MessageType.DEBUG, new Message("Car (" + carData.getUuid().toString() + ") disconnected."));

		EventMessage eMsg = new EventMessage(null, false);
		eMsg.setDescription("unplugged");
		SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg); // TODO Provide UUID?

		exchange.setLocationPath("/.well-known/core"); // Reset
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

		// Add link to charging, if cable check was successfully completed
		if (chargerData.getCableCheckStatus() == 2 && !chargingInit) {
			evCharge = new EvCharge(this.getName(), chargerData, carData);
			evCharge.setVisible(false);
			this.getParent().getParent().getChild("charge").add(evCharge); // TODO
			chargingInit = true;
		}

		// Add link to charge resource
		if (chargingInit && evCharge != null) {
			hal.addLink("charge", new Link(evCharge.getURI()));
		}

		// TODO Check for present current first!
		Form leave = new Form("DELETE", this.getURI(), ""); // TODO define accepts
		hal.addForm("leave", leave);

		// TODO embedded resources?

		return hal;
	}

}
