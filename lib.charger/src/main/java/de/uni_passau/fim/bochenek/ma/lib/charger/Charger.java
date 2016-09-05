package de.uni_passau.fim.bochenek.ma.lib.charger;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;

import de.uni_passau.fim.bochenek.ma.lib.charger.resources.RootResource;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev.EvRoot;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SeCharge;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

/**
 * 
 * @author Martin Bochenek
 *
 */
public class Charger extends CoapServer {

	public Charger(ChargerData chargerData) {
		super();

		// Add SE root resource
		SeCharge seCharge = new SeCharge("charge", chargerData);
		seCharge.setVisible(false);
		this.add(seCharge);

		// Add EV root resource
		EvRoot evRoot = new EvRoot("ev", chargerData);
		evRoot.getAttributes().addResourceType("ev");
		this.add(evRoot);
	}

	@Override
	protected Resource createRoot() {
		return new RootResource();
	}

}
