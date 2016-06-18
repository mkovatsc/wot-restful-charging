package de.uni_passau.fim.bochenek.ma.lib.charger.messages;

import java.util.UUID;

public class StatusMessage extends Message {

	private SeStatus se;
	private EvStatus ev;

	public StatusMessage() {
		this.se = new SeStatus();
		this.ev = new EvStatus();
	}

	public SeStatus getSeStatus() {
		return se;
	}

	public EvStatus getEvStatus() {
		return ev;
	}

	public class SeStatus {

		private double presentVoltage;
		private double presentCurrent;
		private String currentState; // TODO make ENUM

		public SeStatus() {

		}

		public SeStatus(double presentVoltage, double presentCurrent, String state) {
			this.presentVoltage = presentVoltage;
			this.presentCurrent = presentCurrent;
			this.currentState = state;
		}

		public double getPresentVoltage() {
			return presentVoltage;
		}
		public void setPresentVoltage(double presentVoltage) {
			this.presentVoltage = presentVoltage;
		}
		public double getPresentCurrent() {
			return presentCurrent;
		}
		public void setPresentCurrent(double presentCurrent) {
			this.presentCurrent = presentCurrent;
		}
		public String getCurrentState() {
			return currentState;
		}
		public void setCurrentState(String state) {
			this.currentState = state;
		}
	}

	public class EvStatus {

		private UUID uuid;
		private int stateOfCharge;
		private double maximumVoltage;
		private double maximumCurrent;
		private double targetVoltage;
		private double targetCurrent;
		private boolean chargingComplete;
		private boolean readyToCharge;

		public EvStatus() {

		}

		public EvStatus(int stateOfCharge, boolean chargingComplete) {
			this.stateOfCharge = stateOfCharge;
			this.chargingComplete = chargingComplete;
		}

		public UUID getUuid() {
			return uuid;
		}

		public void setUuid(UUID uuid) {
			this.uuid = uuid;
		}

		public int getStateOfCharge() {
			return stateOfCharge;
		}
		public void setStateOfCharge(int stateOfCharge) {
			this.stateOfCharge = stateOfCharge;
		}
		public double getMaximumVoltage() {
			return maximumVoltage;
		}
		public void setMaximumVoltage(double maximumVoltageLimit) {
			this.maximumVoltage = maximumVoltageLimit;
		}
		public double getMaximumCurrent() {
			return maximumCurrent;
		}
		public void setMaximumCurrent(double maximumCurrentLimit) {
			this.maximumCurrent = maximumCurrentLimit;
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

}
