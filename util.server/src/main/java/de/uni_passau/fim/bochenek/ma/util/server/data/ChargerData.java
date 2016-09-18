package de.uni_passau.fim.bochenek.ma.util.server.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.eclipse.californium.core.CoapResource;

public class ChargerData {

	private double maxVoltage;
	private double maxCurrent;
	private double targetVoltage;
	private double targetCurrent;
	private double presentVoltage;
	private double presentCurrent;
	private int cableCheckStatus; // TODO Remove magic numbers, make ENUM! 0 (not running), 1 (running), 2 (completed successful), 3 (error)
	private String currentState; // TODO make ENUM
	private boolean updateOutstanding;

	private Map<String, List<CoapResource>> subscribers;
	private Map<UUID, CarData> carsData;

	public ChargerData() {
		subscribers = new HashMap<String, List<CoapResource>>();
		carsData = new HashMap<UUID, CarData>();
	}

	public synchronized double getMaxVoltage() {
		return maxVoltage;
	}

	public synchronized void setMaxVoltage(double maxVoltage) {
		this.maxVoltage = maxVoltage;
		notifySubscribers("maxVoltage");
	}

	public synchronized double getMaxCurrent() {
		return maxCurrent;
	}

	public synchronized void setMaxCurrent(double maxCurrent) {
		this.maxCurrent = maxCurrent;
		notifySubscribers("maxCurrent");
	}

	public synchronized double getTargetVoltage() {
		return targetVoltage;
	}

	public synchronized void setTargetVoltage(double targetVoltage) {
		this.targetVoltage = targetVoltage;
		notifySubscribers("targetVoltage");
	}

	public synchronized double getTargetCurrent() {
		return targetCurrent;
	}

	public synchronized void setTargetCurrent(double targetCurrent) {
		this.targetCurrent = targetCurrent;
		notifySubscribers("targetCurrent");
	}

	public synchronized double getPresentVoltage() {
		return presentVoltage;
	}

	public synchronized void setPresentVoltage(double presentVoltage) {
		this.presentVoltage = presentVoltage;
		notifySubscribers("presentVoltage");
	}

	public synchronized double getPresentCurrent() {
		return presentCurrent;
	}

	public synchronized void setPresentCurrent(double presentCurrent) {
		this.presentCurrent = presentCurrent;
		notifySubscribers("presentCurrent");
	}

	public synchronized int getCableCheckStatus() {
		return cableCheckStatus;
	}

	public synchronized void setCableCheckStatus(int cableCheckStatus) {
		this.cableCheckStatus = cableCheckStatus;
		notifySubscribers("cableCheckStatus");
	}

	public synchronized String getCurrentState() {
		return currentState;
	}

	public synchronized void setCurrentState(String state) {
		this.currentState = state;
		notifySubscribers("currentState");
	}

	public boolean isUpdateOutstanding() {
		return updateOutstanding;
	}

	public void setUpdateOutstanding(boolean updateOutstanding) {
		this.updateOutstanding = updateOutstanding;
	}

	public void subscribe(CoapResource me, String field) {
		if (!subscribers.containsKey(field)) {
			subscribers.put(field, new LinkedList<CoapResource>());
		}
		subscribers.get(field).add(me);
	}

	private void notifySubscribers(String field) {
		for (String key : subscribers.keySet()) {
			subscribers.get(key).forEach(sub -> sub.changed());
		}
	}

	public CarData addCar(UUID uuid, CarData data) {
		return carsData.put(uuid, data);
	}

	public CarData removeCar(UUID uuid) {
		return carsData.remove(uuid);
	}

	public Set<Entry<UUID, CarData>> getCars() {
		return carsData.entrySet();
	}

	public int connectedCars() {
		return carsData.size();
	}

}
