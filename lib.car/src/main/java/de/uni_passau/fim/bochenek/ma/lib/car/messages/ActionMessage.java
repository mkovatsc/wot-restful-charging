package de.uni_passau.fim.bochenek.ma.lib.car.messages;

public class ActionMessage {

	private String action;
	private String href;
	private String method;
	private int soc;
	private String chargingType;
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

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getSoc() {
		return soc;
	}

	public void setSoc(int soc) {
		this.soc = soc;
	}

	public String getChargingType() {
		return chargingType;
	}

	public void setChargingType(String chargingType) {
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
