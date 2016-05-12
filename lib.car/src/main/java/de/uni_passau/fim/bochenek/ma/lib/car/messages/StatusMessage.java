package de.uni_passau.fim.bochenek.ma.lib.car.messages;

public class StatusMessage {

	private int stateOfCharge;
	private double maximumVoltageLimit;
	private double maximumCurrentLimit;
	private double targetVoltage;
	private double targetCurrent;
	private boolean chargingComplete;

	public StatusMessage() {

	}

	public StatusMessage(int stateOfCharge, boolean chargingComplete) {
		this.stateOfCharge = stateOfCharge;
		this.chargingComplete = chargingComplete;
	}

	public int getStateOfCharge() {
		return stateOfCharge;
	}
	public void setStateOfCharge(int stateOfCharge) {
		this.stateOfCharge = stateOfCharge;
	}
	public double getMaximumVoltageLimit() {
		return maximumVoltageLimit;
	}
	public void setMaximumVoltageLimit(double maximumVoltageLimit) {
		this.maximumVoltageLimit = maximumVoltageLimit;
	}
	public double getMaximumCurrentLimit() {
		return maximumCurrentLimit;
	}
	public void setMaximumCurrentLimit(double maximumCurrentLimit) {
		this.maximumCurrentLimit = maximumCurrentLimit;
	}
	public double getTargetVoltage() {
		return targetVoltage;
	}
	public void setTargetVoltage(double targetVoltage) {
		this.targetVoltage = targetVoltage;
	}
	public double getTargetCurrent() {
		return targetCurrent;
	}
	public void setTargetCurrent(double targetCurrent) {
		this.targetCurrent = targetCurrent;
	}
	public boolean isChargingComplete() {
		return chargingComplete;
	}
	public void setChargingComplete(boolean chargingComplete) {
		this.chargingComplete = chargingComplete;
	}

}
