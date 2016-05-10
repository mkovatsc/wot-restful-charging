package de.uni_passau.fim.bochenek.ma.lib.charger.handler;

import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;

/**
 * TODO
 * 
 * @author Martin Bochenek
 *
 */
public interface IHandler {

	public void callback();
	public void callback(String msg);
	public void callback(MessageType type, Message msg);

}
