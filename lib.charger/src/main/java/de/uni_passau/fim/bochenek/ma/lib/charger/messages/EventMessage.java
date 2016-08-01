package de.uni_passau.fim.bochenek.ma.lib.charger.messages;

import java.util.UUID;

/**
 * TODO Define actual EventMessage
 * 
 * @author Martin Bochenek
 *
 */
public class EventMessage extends Message {

	private UUID uuid;
	private String description;
	private boolean pluggedIn;
	private double targetVoltage;

	public EventMessage(UUID uuid) {
		this.uuid = uuid;
	}

	public EventMessage(UUID uuid, boolean pluggedIn) {
		this.uuid = uuid;
		this.setPluggedIn(pluggedIn);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPluggedIn() {
		return pluggedIn;
	}

	public void setPluggedIn(boolean pluggedIn) {
		this.pluggedIn = pluggedIn;
	}

	public double getTargetVoltage() {
		return targetVoltage;
	}

	public void setTargetVoltage(double targetVoltage) {
		this.targetVoltage = targetVoltage;
	}

}
