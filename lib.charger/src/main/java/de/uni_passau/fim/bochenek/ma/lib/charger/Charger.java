package de.uni_passau.fim.bochenek.ma.lib.charger;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.MessageDeliverer;

import de.uni_passau.fim.bochenek.ma.lib.charger.handler.IHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.TestResource;

/**
 * 
 * @author Martin Bochenek
 *
 */
public class Charger extends CoapServer {

	public Charger() {
		super();

		// Set custom MessageDeliverer
		CustomDeliverer deliverer = new CustomDeliverer(this.getRoot());
		this.setMessageDeliverer(deliverer);

		// Add resources
		this.add(new TestResource("iamyourcharger"));
	}

	/**
	 * 
	 * @param name
	 * @param handler
	 */
	public void registerHandler(String name, IHandler handler) { // TODO > ENUM
		((CustomDeliverer) this.getMessageDeliverer()).registerHandler(name, handler);
	}

	@Override
	public void setMessageDeliverer(MessageDeliverer deliverer) {
		// TODO Don't allow setting new deliverer from outside
		super.setMessageDeliverer(deliverer);
	}

}
