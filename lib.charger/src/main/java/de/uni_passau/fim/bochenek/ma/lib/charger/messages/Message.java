package de.uni_passau.fim.bochenek.ma.lib.charger.messages;

/**
 * TODO
 * 
 * @author Martin Bochenek
 *
 */
public class Message {

	private String message;

	public Message() {

	}

	public Message(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public enum MessageType {
		DEBUG, STATUS, EVENT, ACTION;
	}

}
