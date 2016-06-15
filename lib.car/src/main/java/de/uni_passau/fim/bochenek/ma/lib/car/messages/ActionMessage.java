package de.uni_passau.fim.bochenek.ma.lib.car.messages;

/**
 * TODO Implement actual ActionMessage
 * 
 * @author Martin Bochenek
 *
 */
public class ActionMessage {

	private String action;
	private int soc;
	private double maxVoltage;
	private double maxCurrent;
	private double targetVoltage;
	private double targetCurrent;
	private boolean chargingComplete;
	private boolean readyToCharge;

	public ActionMessage(String notify) {

	}

	public String getAction() {
		return action;
	}

	public void setAction(String notify) {
		this.action = notify;
	}

	public int getSoc() {
		return soc;
	}

	public void setSoc(int soc) {
		this.soc = soc;
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

	public boolean isReadyToCharge() {
		return readyToCharge;
	}

	public void setReadyToCharge(boolean readyToCharge) {
		this.readyToCharge = readyToCharge;
	}

}
