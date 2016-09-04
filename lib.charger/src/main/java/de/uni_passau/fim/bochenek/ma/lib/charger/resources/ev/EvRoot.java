package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;
import de.uni_passau.fim.bochenek.ma.util.server.forms.RegisterForm;

public class EvRoot extends CoapResource {

	private ChargerData chargerData;
	private Map<UUID, CarData> carData;

	public EvRoot(String name, ChargerData chargerData, Map<UUID, CarData> carData) {
		super(name);
		this.chargerData = chargerData;
		this.carData = carData;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, this.getRepresentation().toString(), MediaTypeRegistry.APPLICATION_JSON);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		CarData carData = new CarData();
		UUID uuid = UUID.randomUUID(); // TODO has to be done in the emulator!
		this.carData.put(uuid, carData);
		carData.setUuid(uuid);

		// Parsing the form data and store the information
		Gson gson = new GsonBuilder().create();
		JsonObject formData = gson.fromJson(exchange.getRequestText(), JsonObject.class); // TODO not very robust...
		RegisterForm register = new RegisterForm(formData);
		carData.setSoc(register.getSoc());
		carData.setMaxVoltage(register.getMaxVoltage());
		carData.setMaxCurrent(register.getMaxCurrent());
		carData.setChargingType(register.getChargingType());

		// Setup the new resources that have to be created for the car
		//		Map<String, CoapResource> resources = new HashMap<String, CoapResource>();
		//		resources.put("chargingComplete", new EvChargingComplete("chargingComplete", carData));
		//		resources.put("maxValues", new EvMaxValues("maxValues", carData));
		//		resources.put("stateOfCharge", new EvSoc("stateOfCharge", carData));
		//		resources.put("targetValues", new EvTargetValues("targetValues", carData));

		CoREHalBase actionResult = new CoREHalBase();
		EvID ev = new EvID(uuid.toString(), chargerData, carData);
		ev.setVisible(false);

		//		for (Map.Entry<String, CoapResource> res : resources.entrySet()) {
		//			ev.add(res.getValue());
		//			actionResult.addLink(res.getKey(), new Link(res.getValue().getURI()));
		//		}

		this.add(ev);

		// DEBUG
		SocketHandler socket = SocketHandler.getInstance();
		socket.pushToListeners(MessageType.DEBUG, new Message("New car (" + uuid + ") connected. Cable check should be started!"));
		EventMessage eMsg = new EventMessage(uuid, true);
		eMsg.setDescription("pluggedIn");
		socket.pushToListeners(MessageType.EVENT, eMsg);

		exchange.setLocationPath(ev.getURI());
		exchange.respond(ResponseCode.CREATED, actionResult.toString(), MediaTypeRegistry.APPLICATION_JSON);
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	private CoREHalBase getRepresentation() {
		CoREHalBase hal = new CoREHalBase();
		hal.addLink("self", new Link(this.getURI()));

		hal.addForm("register", new Form("POST", this.getURI(), Utils.getMediaType(RegisterForm.class)));

		for (Resource res : this.getChildren()) {
			hal.addLink(res.getName(), new Link(res.getURI()));
		}

		return hal;
	}

	/**
	 * TODO
	 * 
	 * @param uuid
	 */
	protected void removeCar(UUID uuid) {
		this.carData.remove(uuid);
	}

}
