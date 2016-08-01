package de.uni_passau.fim.bochenek.ma.util.server.data;

public class ChargerData {

	private double maxVoltage;
	private double maxCurrent;
	private double targetVoltage;
	private double targetCurrent;
	private double presentVoltage;
	private double presentCurrent;
	private int cableCheckStatus; // TODO Remove magic numbers, make ENUM! 0 (not running), 1 (running), 2 (completed successful), 3 (error)
	private String currentState; // TODO make ENUM

	public ChargerData() {
		// TODO
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

	public synchronized double getPresentVoltage() {
		return presentVoltage;
	}

	public synchronized void setPresentVoltage(double presentVoltage) {
		this.presentVoltage = presentVoltage;
	}

	public synchronized double getPresentCurrent() {
		return presentCurrent;
	}

	public synchronized void setPresentCurrent(double presentCurrent) {
		this.presentCurrent = presentCurrent;
	}

	public synchronized int getCableCheckStatus() {
		return cableCheckStatus;
	}

	public synchronized void setCableCheckStatus(int cableCheckStatus) {
		this.cableCheckStatus = cableCheckStatus;
	}

	public synchronized String getCurrentState() {
		return currentState;
	}

	public synchronized void setCurrentState(String state) {
		this.currentState = state;
	}

}
