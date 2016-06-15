package de.uni_passau.fim.bochenek.ma.lib.car;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.jetty.websocket.api.Session;

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
		logger.log(Level.INFO, "New car connected.");
	}

	@Override
	public UUID plugIn() {
		this.uuid = UUID.randomUUID(); // TODO obtain from charger
		return this.uuid;
	}

	@Override
	public boolean chargeParameterDiscovery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cableCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean preCharge() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean powerDelivery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean currentDemand() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean weldingDetection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopSession() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unplug() {
		// TODO Auto-generated method stub

	}

	public void sendToCharger(String message) {
		client.setURI(baseURI + "/iamyourcharger");
		client.post(message, MediaTypeRegistry.TEXT_PLAIN);
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
