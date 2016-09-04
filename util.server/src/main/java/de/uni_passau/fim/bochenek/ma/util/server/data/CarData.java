package de.uni_passau.fim.bochenek.ma.util.server.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.californium.core.CoapResource;

import de.uni_passau.fim.bochenek.ma.util.server.enums.ChargingType;

public class CarData {

	private UUID uuid;
	private ChargingType chargingType;
	private int soc;
	private double maxVoltage;
	private double maxCurrent;
	private double targetVoltage;
	private double targetCurrent;
	private boolean readyToCharge;
	private boolean chargingComplete;
	private Map<String, CoapResource> bookmarks;

	public CarData() {
		//TODO
		this.bookmarks = new HashMap<String, CoapResource>();
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
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

	public synchronized double getTargetVoltage() {
		return targetVoltage;
	}

	public synchronized void setTargetVoltage(double targetVoltage) {
		this.targetVoltage = targetVoltage;
	}

	public synchronized double getTargetCurrent() {
		return targetCurrent;
	}

	public synchronized void setTargetCurrent(double targetCurrent) {
		this.targetCurrent = targetCurrent;
	}

	public synchronized boolean isReadyToCharge() {
		return readyToCharge;
	}

	public synchronized void setReadyToCharge(boolean readyToCharge) {
		this.readyToCharge = readyToCharge;
	}

	public synchronized boolean isChargingComplete() {
		return chargingComplete;
	}

	public synchronized void setChargingComplete(boolean chargingComplete) {
		this.chargingComplete = chargingComplete;
	}

	public Map<String, CoapResource> getBookmarks() {
		return bookmarks;
	}

}
