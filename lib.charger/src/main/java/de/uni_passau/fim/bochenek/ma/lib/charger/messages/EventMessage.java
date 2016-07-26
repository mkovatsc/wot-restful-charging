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
	private boolean pluggedIn;

	public EventMessage(UUID uuid, boolean pluggedIn) {
		this.setPluggedIn(pluggedIn);
	}

	public boolean isPluggedIn() {
		return pluggedIn;
	}

	public void setPluggedIn(boolean pluggedIn) {
		this.pluggedIn = pluggedIn;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

}
