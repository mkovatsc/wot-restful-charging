package de.uni_passau.fim.bochenek.ma.lib.car;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ch.ethz.inf.vs.hypermedia.corehal.block.CoREHalBaseResourceFuture;
import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import de.uni_passau.fim.bochenek.ma.lib.car.handler.ObserveHandler;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;

/**
 * TODO Implement some kind of state machine to "force" the right order for
 * method calls
 * 
 * @author Martin Bochenek
 *
 */
public class Car {

	private CoapClient client; // TODO Extend CoapClient?
	private CoapResponse lastRes;

	private UUID uuid;
	private Session session;
	private CarData carData;

	private static Logger logger = Logger.getLogger(Car.class.getName());

	public Car(String chargerURI) {
		carData = new CarData();
		client = new CoapClient(chargerURI); // TODO Server not available?

		// DEBUG
		logger.info("New CarUI established websocket connection.");
	}

	public Set<WebLink> plugIn() {
		return client.discover();
	}

	public CoREHalBase follow(String href) {
		client.setURI(href);
		return this.getCoREHal();
	}

	public CoapResponse submitForm(String href, String method, JsonObject data) {
		client.setURI(href);

		switch (method) {
			case "POST" :
				lastRes = client.post(new GsonBuilder().create().toJson(data), MediaTypeRegistry.APPLICATION_JSON); // TODO data could be null!
				return lastRes;
			case "PUT" :
				lastRes = client.put(new GsonBuilder().create().toJson(data), MediaTypeRegistry.APPLICATION_JSON); // TODO data could be null!
				return lastRes;
			case "DELETE" :
				lastRes = client.delete();
				return lastRes;
			default :
				return null; // TODO
		}
	}

	public boolean observe(String href) {
		carData.getObserves().put(href, client.observe(new ObserveHandler(this)));
		return false; // TODO
	}

	public boolean cancelObserve(String href) {
		carData.getObserves().remove(href).reactiveCancel(); // TODO could be null or already cancelled?
		return false; // TODO
	}

	public CoREHalBase getCoREHal() {
		CoREHalBase hal = new CoREHalBase();
		lastRes = client.get();
		try {
			hal = new CoREHalBaseResourceFuture().deserialize(lastRes.getResponseText());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return hal;
	}

	public void unplug() {
		// TODO

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

	public CarData getData() {
		return carData;
	}

	// TODO
	public String getCurrentLocation() {
		return client.getURI();
	}

	// TODO
	public CoapResponse getLastResponse() {
		return lastRes;
	}

}
