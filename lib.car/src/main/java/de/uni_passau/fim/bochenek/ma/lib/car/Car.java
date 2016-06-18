package de.uni_passau.fim.bochenek.ma.lib.car;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * TODO
 * 
 * @author Martin Bochenek
 *
 */
public class Car implements ICar {

	private UUID uuid;
	private Session session;

	private String baseURI;
	private CoapClient client;

	private static Logger logger = Logger.getLogger(Car.class.getName());

	public Car(String chargerURI) {
		client = new CoapClient();
		baseURI = chargerURI; // TODO check for validity

		// DEBUG
		logger.info("New CarUI established websocket connection.");
	}

	@Override
	public UUID plugIn() {
		client.setURI(baseURI + "/ev");
		CoapResponse res = client.post("", MediaTypeRegistry.UNDEFINED);
		this.uuid = UUID.fromString(res.getOptions().getLocationPath().get(1)); // TODO remove magic number

		// DEBUG
		logger.log(Level.INFO, "Car with UUID {0} plugged in.", new Object[]{this.uuid});

		return this.uuid;
	}

	@Override
	public boolean chargeParameterDiscovery(int soc, double maxVoltage, double maxCurrent) {
		Gson gson = new GsonBuilder().create();

		JsonObject stateOfCharge = new JsonObject();
		stateOfCharge.addProperty("soc", soc);
		client.setURI(baseURI + "/ev/" + this.uuid + "/stateOfCharge");
		client.post(gson.toJson(stateOfCharge), MediaTypeRegistry.APPLICATION_JSON);

		JsonObject maxVals = new JsonObject();
		maxVals.addProperty("maxVoltage", maxVoltage);
		maxVals.addProperty("maxCurrent", maxCurrent);
		client.setURI(baseURI + "/ev/" + this.uuid + "/maxValues");
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

		return false;
	}

	@Override
	public boolean preCharge(double targetVoltage, double targetCurrent) {
		Gson gson = new GsonBuilder().create();
		JsonObject targetVals = new JsonObject();
		targetVals.addProperty("targetVoltage", targetVoltage);
		targetVals.addProperty("targetCurrent", targetCurrent);
		client.setURI(baseURI + "/ev/" + this.uuid + "/targetValues");
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
		client.setURI(baseURI + "/ev/" + this.uuid + "/chargingComplete");
		client.post(gson.toJson(tmp1), MediaTypeRegistry.APPLICATION_JSON);

		JsonObject tmp2 = new JsonObject();
		tmp2.addProperty("readyToCharge", readyToCharge);
		client.setURI(baseURI + "/ev/" + this.uuid + "/readyToCharge");
		client.post(gson.toJson(tmp2), MediaTypeRegistry.APPLICATION_JSON);

		// DEBUG
		logger.log(Level.INFO, "{0}: Requested power delivery.", new Object[]{this.uuid});

		return false; // TODO
	}

	@Override
	public boolean currentDemand(int soc, double targetVoltage, double targetCurrent, boolean chargingComplete) {
		Gson gson = new GsonBuilder().create();
		JsonObject targetVals = new JsonObject();
		targetVals.addProperty("targetVoltage", targetVoltage);
		targetVals.addProperty("targetCurrent", targetCurrent);
		client.setURI(baseURI + "/ev/" + this.uuid + "/targetValues");
		client.post(gson.toJson(targetVals), MediaTypeRegistry.APPLICATION_JSON);

		JsonObject stateOfCharge = new JsonObject();
		stateOfCharge.addProperty("soc", soc);
		client.setURI(baseURI + "/ev/" + this.uuid + "/stateOfCharge");
		client.post(gson.toJson(stateOfCharge), MediaTypeRegistry.APPLICATION_JSON);

		// DEBUG
		logger.log(Level.INFO, "{0}: Sent current demand request.", new Object[]{this.uuid});

		return false; // TODO
	}

	@Override
	public boolean weldingDetection() {
		// TODO Auto-generated method stub

		// DEBUG
		logger.log(Level.INFO, "{0}: Asked for welding detection.", new Object[]{this.uuid});

		return false;
	}

	@Override
	public boolean stopSession() {
		// TODO Auto-generated method stub

		// DEBUG
		logger.log(Level.INFO, "{0}: Stopped the session.", new Object[]{this.uuid});

		return false;
	}

	@Override
	public void unplug() {
		client.setURI(baseURI + "/ev/" + this.uuid);
		client.delete();

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

	public UUID getUuid() {
		return uuid;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
