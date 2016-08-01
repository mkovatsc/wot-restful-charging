package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import black.door.hate.HalRepresentation;
import black.door.hate.HalResource;
import black.door.hate.LinkOrResource;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.EventMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;
import de.uni_passau.fim.bochenek.ma.util.server.enums.ChargingType;
import black.door.hate.HalRepresentation.HalRepresentationBuilder;

public class EvRoot extends CoapResource implements HalResource {

	private ChargerData chargerData;
	private Map<UUID, CarData> cars;

	public EvRoot(String name, ChargerData chargerData, Map<UUID, CarData> cars) {
		super(name);
		this.chargerData = chargerData;
		this.cars = cars;
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
	public void handlePOST(CoapExchange exchange) {
		CarData carData = new CarData();
		UUID uuid = UUID.randomUUID(); // TODO has to be done in the emulator!
		this.cars.put(uuid, carData);

		// TODO not very robust :P
		Gson gson = new GsonBuilder().create();
		JsonObject basicInfo = gson.fromJson(exchange.getRequestText(), JsonObject.class);
		carData.setSoc(basicInfo.get("soc").getAsInt());
		carData.setMaxVoltage(basicInfo.get("maxVoltage").getAsDouble());
		carData.setMaxCurrent(basicInfo.get("maxCurrent").getAsDouble());
		carData.setChargingType(ChargingType.valueOf(basicInfo.get("chargingType").getAsString()));

		Map<String, CoapResource> resources = new HashMap<String, CoapResource>();
		resources.put("chargingComplete", new EvChargingComplete("chargingComplete", carData));
		resources.put("maxValues", new EvMaxValues("maxValues", carData));
		resources.put("stateOfCharge", new EvSoc("stateOfCharge", carData));
		resources.put("targetValues", new EvTargetValues("targetValues", carData));

		String actionResult = null;
		HalRepresentationBuilder result = HalRepresentation.builder();
		EvID ev = new EvID(uuid.toString(), chargerData, carData);

		for (Map.Entry<String, CoapResource> res : resources.entrySet()) {
			ev.add(res.getValue());
			result.addLink(res.getKey(), (LinkOrResource) res.getValue());
		}

		this.add(ev);
		try {
			actionResult = result.build().serialize();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// DEBUG
		SocketHandler socket = SocketHandler.getInstance();
		socket.pushToListeners(MessageType.DEBUG, new Message("New car (" + uuid + ") connected. Cable check should be started!"));
		EventMessage eMsg = new EventMessage(uuid, true);
		eMsg.setDescription("pluggedIn");
		socket.pushToListeners(MessageType.EVENT, eMsg);

		exchange.setLocationPath(ev.getURI());
		exchange.respond(ResponseCode.CREATED, actionResult, MediaTypeRegistry.APPLICATION_JSON);
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

		for (Resource res : this.getChildren()) {
			hal.addLink(res.getName(), (HalResource) res);
		}

		return hal;
	}

	/**
	 * TODO
	 * 
	 * @param uuid
	 */
	protected void removeCar(UUID uuid) {
		this.cars.remove(uuid);
	}

}
