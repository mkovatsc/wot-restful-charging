package de.uni_passau.fim.bochenek.ma.util.server;

import de.uni_passau.fim.bochenek.ma.util.server.enums.ChargingType;

public class CarData {

	private ChargingType chargingType;
	private int soc;
	private double maxVoltage;
	private double maxCurrent;

	public CarData() {
		//TODO
	}

	public synchronized ChargingType getChargingType() {
		return chargingType;
	}
	public synchronized void setChargingType(ChargingType chargingType) {
		this.chargingType = chargingType;
	}
	public synchronized int getSoc() {
		return soc;
	}
	public synchronized void setSoc(int soc) {
		this.soc = soc;
	}
	public synchronized double getMaxVoltage() {
		return maxVoltage;
	}
	public synchronized void setMaxVoltage(double maxVoltage) {
		this.maxVoltage = maxVoltage;
	}
	public synchronized double getMaxCurrent() {
		return maxCurrent;
	}
	public synchronized void setMaxCurrent(double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}

}
