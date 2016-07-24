package de.uni_passau.fim.bochenek.ma.lib.charger.messages;

/**
 * TODO Define actual EventMessage
 * 
 * @author Martin Bochenek
 *
 */
public class EventMessage extends Message {

	private boolean pluggedIn;

	public EventMessage(boolean pluggedIn) {
		this.setPluggedIn(pluggedIn);
	}

	public boolean isPluggedIn() {
		return pluggedIn;
	}

	public void setPluggedIn(boolean pluggedIn) {
		this.pluggedIn = pluggedIn;
	}

}
