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
public class Car {

	private UUID uuid;
	private Session session;

	private String baseURI;
	private CoapClient client;

	private static Logger logger = Logger.getLogger(Car.class.getName());

	public Car(String chargerURI) {
		uuid = UUID.randomUUID();
		client = new CoapClient();
		baseURI = chargerURI; // TODO check for validity

		logger.log(Level.INFO, "New car with UUID {0} connected.", new Object[]{uuid.toString()});
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
