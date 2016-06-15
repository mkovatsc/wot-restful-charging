package de.uni_passau.fim.bochenek.ma.lib.car;

import java.util.UUID;

/**
 * TODO Handler / Callbacks for CoAP responses etc.
 * 
 * @author Martin Bochenek
 *
 */
public interface ICar {

	// TODO Javadoc
	public UUID plugIn();

	// TODO Javadoc
	public boolean chargeParameterDiscovery();

	// TODO Javadoc
	public boolean cableCheck();

	// TODO Javadoc
	public boolean preCharge();

	// TODO Javadoc
	public boolean powerDelivery();

	// TODO Javadoc
	public boolean currentDemand();

	// TODO Javadoc
	public boolean weldingDetection();

	// TODO Javadoc
	public boolean stopSession();

	// TODO Javadoc
	public void unplug();

}
