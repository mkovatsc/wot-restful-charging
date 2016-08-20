package de.uni_passau.fim.bochenek.ma.lib.car;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.enums.ChargingType;

/**
 * TODO Implement some kind of state machine to "force" the right order for
 * method calls
 * 
 * @author Martin Bochenek
 *
 */
public class Car implements ICar { // TODO Extend CoapClient?

	private UUID uuid;
	private Session session;
	private CarData carData; // TODO Think about dependency injection!

	private CoapClient client;
	private HashMap<String, String> resMap;
	private HashMap<String, CoapObserveRelation> observed; // TODO Better index

	private static Logger logger = Logger.getLogger(Car.class.getName());

	public Car(String chargerURI) {
		carData = new CarData();
		client = new CoapClient(chargerURI); // TODO Server not available?
		resMap = new HashMap<String, String>();
		observed = new HashMap<String, CoapObserveRelation>();

		// Discover the entry URL for EVs
		for (WebLink wl : client.discover()) {
			if (wl.getAttributes().getResourceTypes().contains("ev")) {
				resMap.put("ev", wl.getURI());
				break;
			}
		}

		// DEBUG
		logger.info("New CarUI established websocket connection.");
	}

	@Override
	public UUID plugIn(ChargingType chargingType, int soc, double maxVoltage, double maxCurrent) {
		Gson gson = new GsonBuilder().create();

		JsonObject basicInfo = new JsonObject();
		basicInfo.addProperty("chargingType", chargingType.toString());
		basicInfo.addProperty("soc", soc);
		basicInfo.addProperty("maxVoltage", maxVoltage);
		basicInfo.addProperty("maxCurrent", maxCurrent);
		client.setURI(resMap.get("ev"));
		CoapResponse res = client.post(gson.toJson(basicInfo), MediaTypeRegistry.APPLICATION_JSON);

		this.uuid = UUID.fromString(res.getOptions().getLocationPath().get(1)); // TODO remove magic number
		resMap.put("ev_self", "/" + res.getOptions().getLocationPathString());
		resolveAndAddRel(res.getResponseText(), "stateOfCharge", "maxValues");

		// DEBUG
		logger.log(Level.INFO, "Car with UUID {0} plugged in.", new Object[]{this.uuid});

		return this.uuid;
	}

	@Override
	public List<String> checkAvailabeActions() {
		client.setURI(resMap.get("ev_self")); // TODO what if key doesn't exist?
		CoapResponse res = client.get();

		JsonParser parser = new JsonParser();
		JsonObject tmp = parser.parse(res.getResponseText()).getAsJsonObject();

		List<String> refs = new LinkedList<String>();

		if (tmp.has("_links")) {
			JsonObject links = tmp.getAsJsonObject("_links");
			links.entrySet().forEach(e -> refs.add(e.getKey()));

			links.remove("self"); // We should know this already
			links.entrySet().forEach(entry -> resMap.put(entry.getKey(), entry.getValue().getAsJsonObject().get("href").getAsString())); // TODO ugly
		}

		logger.info(refs.toString());
		return refs;
	}

	@Override
	public String setTargetVoltage(double targetVoltage) {
		carData.setTargetVoltage(targetVoltage);

		Gson gson = new GsonBuilder().create();

		JsonObject tV = new JsonObject();
		tV.addProperty("targetVoltage", targetVoltage);
		client.setURI("/charge/" + uuid.toString()); // TODO only for debugging, URI must be resolved from resMap / given by UI!!
		CoapResponse res = client.post(gson.toJson(tV), MediaTypeRegistry.APPLICATION_JSON);
		String locPath = res.getOptions().getLocationPathString();
		resMap.put("chargeProc", "/" + locPath);

		return locPath;
	}

	@Override
	public List<String> lookupChargingProcess() { // TODO Maybe build generic lookup
		client.setURI(resMap.get("chargeProc"));
		CoapResponse res = client.get();

		JsonParser parser = new JsonParser();
		JsonObject tmp = parser.parse(res.getResponseText()).getAsJsonObject();

		List<String> refs = new LinkedList<String>();

		if (tmp.has("_links")) {
			JsonObject links = tmp.getAsJsonObject("_links");
			links.entrySet().forEach(e -> refs.add(e.getKey()));

			links.remove("self"); // We should know this already
			links.entrySet().forEach(entry -> resMap.put(entry.getKey(), entry.getValue().getAsJsonObject().get("href").getAsString())); // TODO ugly
		}

		// TODO Establish OBSERVE relationship, push incoming messages directly to UI
		//		client.setURI(resMap.get("pV"));

		logger.info(refs.toString());
		return refs;
	}

	@Override
	public boolean stopChargingProcess() {
		client.setURI(resMap.get("chargeProc"));
		client.delete();

		resMap.remove("chargeProc");

		return false; // TODO
	}

	@Override
	public boolean chargeParameterDiscovery(int soc, double maxVoltage, double maxCurrent) {
		Gson gson = new GsonBuilder().create();

		JsonObject stateOfCharge = new JsonObject();
		stateOfCharge.addProperty("soc", soc);
		client.setURI(resMap.get("stateOfCharge"));
		client.post(gson.toJson(stateOfCharge), MediaTypeRegistry.APPLICATION_JSON);

		JsonObject maxVals = new JsonObject();
		maxVals.addProperty("maxVoltage", maxVoltage);
		maxVals.addProperty("maxCurrent", maxCurrent);
		client.setURI(resMap.get("maxValues"));
		client.post(gson.toJson(maxVals), MediaTypeRegistry.APPLICATION_JSON);

		// DEBUG
		logger.log(Level.INFO, "{0}: Triggered charge parameter discovery.", new Object[]{this.uuid});

		return false; // TODO
	}

	@Override
	public boolean cableCheck() {
		// TODO Auto-generated method stub

		// DEBUG
		logger.log(Level.INFO, "{0}: Initiated cable check.", new Object[]{this.uuid});

		return false; // TODO
	}

	@Override
	public boolean preCharge(double targetVoltage, double targetCurrent) {

		// Establish observe relation to charger values
		CoapHandler handler = new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				JsonObject json = new Gson().fromJson(response.getResponseText(), JsonObject.class);
				String tmp = String.format(Locale.US, "{\"voltage\": %.2f, \"current\": %.2f}", json.get("voltage").getAsFloat(), json.get("current").getAsFloat());
				sendToCar("SEVALUES", tmp);

				// Cancel observe and remove from maps
				CoapObserveRelation rel = observed.remove("preCharge");
				if (rel != null) {
					rel.reactiveCancel();
				}
			}

			@Override
			public void onError() {
				logger.info("Failed to observe resource!");
			}
		};
		client.setURI("/se/presentValues");
		observed.put("preCharge", client.observeAndWait(handler));

		Gson gson = new GsonBuilder().create();
		JsonObject targetVals = new JsonObject();
		targetVals.addProperty("targetVoltage", targetVoltage);
		targetVals.addProperty("targetCurrent", targetCurrent);
		client.setURI(resMap.get("ev_self") + "/targetValues");
		client.post(gson.toJson(targetVals), MediaTypeRegistry.APPLICATION_JSON);

		// DEBUG
		logger.log(Level.INFO, "{0}: Sent pre charge request.", new Object[]{this.uuid});

		return false; // TODO
	}

	@Override
	public boolean powerDelivery(boolean chargingComplete, boolean readyToCharge) {
		Gson gson = new GsonBuilder().create();

		JsonObject tmp1 = new JsonObject();
		tmp1.addProperty("chargingComplete", chargingComplete);
		client.setURI(resMap.get("ev_self") + "/chargingComplete");
		client.post(gson.toJson(tmp1), MediaTypeRegistry.APPLICATION_JSON);

		JsonObject tmp2 = new JsonObject();
		tmp2.addProperty("readyToCharge", readyToCharge);
		client.setURI(resMap.get("ev_self") + "/readyToCharge");
		client.post(gson.toJson(tmp2), MediaTypeRegistry.APPLICATION_JSON);

		// DEBUG
		logger.log(Level.INFO, "{0}: Requested power delivery.", new Object[]{this.uuid});

		return false; // TODO
	}

	@Override
	public boolean currentDemand(int soc, double targetVoltage, double targetCurrent, boolean chargingComplete) {
		// Implementation for old approach following
		//		Gson gson = new GsonBuilder().create();
		//		JsonObject targetVals = new JsonObject();
		//		targetVals.addProperty("targetVoltage", targetVoltage);
		//		targetVals.addProperty("targetCurrent", targetCurrent);
		//		client.setURI(resMap.get("ev_self") + "/targetValues");
		//		client.post(gson.toJson(targetVals), MediaTypeRegistry.APPLICATION_JSON);
		//
		//		JsonObject stateOfCharge = new JsonObject();
		//		stateOfCharge.addProperty("soc", soc);
		//		client.setURI(resMap.get("stateOfCharge"));
		//		client.post(gson.toJson(stateOfCharge), MediaTypeRegistry.APPLICATION_JSON);

		JsonObject tV = new JsonObject();
		tV.addProperty("targetVoltage", targetVoltage);
		client.setURI(resMap.get("tV"));
		client.put(tV.toString(), MediaTypeRegistry.APPLICATION_JSON);

		JsonObject tC = new JsonObject();
		tC.addProperty("targetCurrent", targetCurrent);
		client.setURI(resMap.get("tC"));
		client.put(tC.toString(), MediaTypeRegistry.APPLICATION_JSON);

		JsonObject stateOfCharge = new JsonObject();
		stateOfCharge.addProperty("soc", soc);
		client.setURI(resMap.get("stateOfCharge")); // TODO Another resource?
		client.post(stateOfCharge.toString(), MediaTypeRegistry.APPLICATION_JSON);

		// DEBUG
		logger.log(Level.INFO, "{0}: Sent current demand request.", new Object[]{this.uuid});

		return false; // TODO
	}

	@Override
	public boolean weldingDetection() {
		// TODO Auto-generated method stub

		// DEBUG
		logger.log(Level.INFO, "{0}: Asked for welding detection.", new Object[]{this.uuid});

		return false; // TODO
	}

	@Override
	public boolean stopSession() {
		// TODO Auto-generated method stub

		// DEBUG
		logger.log(Level.INFO, "{0}: Stopped the session.", new Object[]{this.uuid});

		return false; // TODO
	}

	@Override
	public void unplug() {
		client.setURI(resMap.get("ev_self"));
		client.delete();

		resMap.remove("ev_self");

		// DEBUG
		logger.log(Level.INFO, "Car with UUID {0} unplugged.", new Object[]{this.uuid});
	}

	public void sendToCar(String message) {
		try {
			session.getRemote().sendString(message);
		} catch (IOException e) {
			// TODO Session invalid? Store cars?
			e.printStackTrace();
		}
	}

	private void sendToCar(String type, String data) {
		String tmp = String.format("{\"type\":\"%s\", \"data\": %s}", type, data); // TODO
		this.sendToCar(tmp);
	}

	/**
	 * TODO
	 * 
	 * @param response
	 * @param rels
	 */
	private void resolveAndAddRel(String response, String... rels) {
		JsonObject links = new Gson().fromJson(response, JsonObject.class).getAsJsonObject("_links");
		for (String rel : rels) {
			if (links.has(rel)) {
				this.resMap.put(rel, links.getAsJsonObject(rel).get("href").getAsString());
			}
		}
	}

	public UUID getUuid() {
		return uuid;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public CarData getData() {
		return carData;
	}

}
