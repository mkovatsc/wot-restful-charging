package de.uni_passau.fim.bochenek.ma.lib.car.messages;

import de.uni_passau.fim.bochenek.ma.lib.car.enums.ChargingType;

/**
 * TODO Define actual EventMessage
 * 
 * @author Martin Bochenek
 *
 */
public class EventMessage extends Message {

	private boolean pluggedIn;
	private int soc;
	private ChargingType chargingType;
	private double maxVoltage;
	private double maxCurrent;

	public EventMessage(boolean pluggedIn) {
		this.setPluggedIn(pluggedIn);
	}

	public boolean isPluggedIn() {
		return pluggedIn;
	}

	public void setPluggedIn(boolean pluggedIn) {
		this.pluggedIn = pluggedIn;
	}

	public int getSoc() {
		return soc;
	}

	public void setSoc(int soc) {
		this.soc = soc;
	}

	public ChargingType getChargingType() {
		return chargingType;
	}

	public void setChargingType(ChargingType chargingType) {
		this.chargingType = chargingType;
	}

	public double getMaxVoltage() {
		return maxVoltage;
	}

	public void setMaxVoltage(double maxVoltage) {
		this.maxVoltage = maxVoltage;
	}

	public double getMaxCurrent() {
		return maxCurrent;
	}

	public void setMaxCurrent(double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}

}
