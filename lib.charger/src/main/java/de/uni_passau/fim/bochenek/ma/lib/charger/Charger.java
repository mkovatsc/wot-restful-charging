package de.uni_passau.fim.bochenek.ma.lib.charger;

import java.util.Map;
import java.util.UUID;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;

import de.uni_passau.fim.bochenek.ma.lib.charger.resources.RootResource;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.charge.SeCharge;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev.EvRoot;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SeMaxValues;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SePresentValues;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.se.SeRoot;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

/**
 * 
 * @author Martin Bochenek
 *
 */
public class Charger extends CoapServer {

	public Charger(ChargerData chargerData, Map<UUID, CarData> carData) {
		super();

		// Add initial static resources
		SeRoot seRoot = new SeRoot("se");
		seRoot.add(new SeMaxValues("maxValues", chargerData));
		seRoot.add(new SePresentValues("presentValues", chargerData));
		this.add(seRoot);
		this.add(new SeCharge("charge", chargerData));
		this.add(new EvRoot("ev", chargerData, carData));
	}

	@Override
	protected Resource createRoot() {
		return new RootResource();
	}

}
