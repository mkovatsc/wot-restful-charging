package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
//import org.eclipse.californium.core.server.resources.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;

import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.EventMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;

public class EvID extends CoapResource implements HalResource {

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
		try {
			exchange.respond(ResponseCode.CONTENT, this.asEmbedded().serialize(), MediaTypeRegistry.APPLICATION_JSON);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		((EvRoot) this.getParent()).removeCar(UUID.fromString(this.getName())); // TODO maybe make this methods of the model and inject
		this.delete();
		evCharge.delete();

		// DEBUG
		SocketHandler socket = SocketHandler.getInstance();
		socket.pushToListeners(MessageType.DEBUG, new Message("Car (" + carData.getUuid().toString() + ") disconnected."));

		EventMessage eMsg = new EventMessage(null, false);
		eMsg.setDescription("unplugged");
		SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg); // TODO Provide UUID?

		exchange.respond(ResponseCode.DELETED);
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
		hal.addProperty("id", this.getName());

		// Add link to charging, if cable check was successfully completed
		if (chargerData.getCableCheckStatus() == 2 && !chargingInit) {
			evCharge = new EvCharge(this.getName(), chargerData, carData);
			this.getParent().getParent().getChild("charge").add(evCharge); // TODO
			chargingInit = true;
		}

		// Add link to charge resource
		if (chargingInit && evCharge != null) {
			hal.addLink("charge", evCharge);
		}

		//		for (Resource res : this.getChildren()) {
		//
		//			// TODO Maybe to much information due to recursive nature
		//			hal.addEmbedded(res.getName(), (HalResource) res);
		//		}

		return hal;
	}

}
