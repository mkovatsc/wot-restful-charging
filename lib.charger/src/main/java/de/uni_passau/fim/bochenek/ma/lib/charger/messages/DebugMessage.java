package de.uni_passau.fim.bochenek.ma.lib.charger.messages;

public class DebugMessage extends Message {

	private String message;

	public DebugMessage(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
