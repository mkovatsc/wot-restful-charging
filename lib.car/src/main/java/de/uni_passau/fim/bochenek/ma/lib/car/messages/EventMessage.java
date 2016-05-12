package de.uni_passau.fim.bochenek.ma.lib.car.messages;

/**
 * TODO Define actual EventMessage
 * 
 * @author Martin Bochenek
 *
 */
public class EventMessage extends Message {

	private String message;

	public EventMessage(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
