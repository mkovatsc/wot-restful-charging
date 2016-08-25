package de.uni_passau.fim.bochenek.ma.lib.car;

import java.util.List;
import java.util.UUID;

import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import de.uni_passau.fim.bochenek.ma.util.server.enums.ChargingType;

/**
 * TODO Handler / Callbacks for CoAP responses etc.
 * 
 * @author Martin Bochenek
 *
 */
public interface ICar {

	// TODO Javadoc
	public UUID plugIn(ChargingType chargingType, int soc, double maxVoltage, double maxCurrent);

	// TODO Javadoc
	public List<String> checkAvailabeActions();

	// TODO Javadoc
	public CoREHalBase getCoREHal();

	// TODO Javadoc
	public CoREHalBase getCoREHal(String rel);

	// TODO Javadoc
	public String setTargetVoltage(double targetVoltage);

	// TODO Javadoc
	public List<String> lookupChargingProcess();

	// TODO Javadoc
	public boolean stopChargingProcess();

	// TODO Javadoc
	public boolean chargeParameterDiscovery(int soc, double maxVoltage, double maxCurrent);

	// TODO Javadoc
	public boolean cableCheck();

	// TODO Javadoc
	public boolean preCharge(double targetVoltage, double targetCurrent);

	// TODO Javadoc
	public boolean powerDelivery(boolean chargingComplete, boolean readyToCharge);

	// TODO Javadoc
	public boolean currentDemand(int soc, double targetVoltage, double targetCurrent, boolean chargingComplete);

	// TODO Javadoc
	public boolean weldingDetection();

	// TODO Javadoc
	public boolean stopSession();

	// TODO Javadoc
	public void unplug();

}
