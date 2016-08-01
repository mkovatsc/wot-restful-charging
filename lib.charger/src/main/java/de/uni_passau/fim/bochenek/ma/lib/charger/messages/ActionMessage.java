package de.uni_passau.fim.bochenek.ma.lib.charger.messages;

/**
 * TODO Implement actual ActionMessage
 * 
 * @author Martin Bochenek
 *
 */
public class ActionMessage {

	private String action;
	private int cableCheckStatus;
	private double presentVoltage;

	public ActionMessage(String notify) {

	}

	public String getAction() {
		return action;
	}

	public void setAction(String notify) {
		this.action = notify;
	}

	public int getCableCheckStatus() {
		return cableCheckStatus;
	}

	public void setCableCheckStatus(int cableCheckStatus) {
		this.cableCheckStatus = cableCheckStatus;
	}

	public double getPresentVoltage() {
		return presentVoltage;
	}

	public void setPresentVoltage(double presentVoltage) {
		this.presentVoltage = presentVoltage;
	}

}
