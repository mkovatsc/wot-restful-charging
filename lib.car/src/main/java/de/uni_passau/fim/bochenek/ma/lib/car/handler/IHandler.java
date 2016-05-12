package de.uni_passau.fim.bochenek.ma.lib.car.handler;

import de.uni_passau.fim.bochenek.ma.lib.car.messages.Message;
import de.uni_passau.fim.bochenek.ma.lib.car.messages.Message.MessageType;

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
