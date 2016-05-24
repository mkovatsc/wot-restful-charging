package de.uni_passau.fim.bochenek.ma.lib.charger;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.MessageDeliverer;
import org.eclipse.californium.core.server.resources.Resource;

import de.uni_passau.fim.bochenek.ma.lib.charger.handler.IHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.RootResource;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev.EvRoot;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SeMaxValues;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SePilotVoltage;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SePresentValues;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SeReadyToCharge;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SeRoot;

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

		// Add initial static resources
		SeRoot seRoot = new SeRoot("se");
		seRoot.add(new SeMaxValues("maxValues"));
		seRoot.add(new SePilotVoltage("pilotVoltage"));
		seRoot.add(new SePresentValues("presentValues"));
		seRoot.add(new SeReadyToCharge("readyToCharge"));
		this.add(seRoot);
		this.add(new EvRoot("ev"));
	}

	/**
	 * 
	 * @param name
	 * @param handler
	 */
	public void registerHandler(MessageType type, IHandler handler) {
		((CustomDeliverer) this.getMessageDeliverer()).registerHandler(type, handler);
	}

	@Override
	public void setMessageDeliverer(MessageDeliverer deliverer) {
		// TODO Don't allow setting new deliverer from outside
		super.setMessageDeliverer(deliverer);
	}

	@Override
	protected Resource createRoot() {
		return new RootResource();
	}

}
