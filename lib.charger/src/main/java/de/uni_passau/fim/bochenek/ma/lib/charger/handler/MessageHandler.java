package de.uni_passau.fim.bochenek.ma.lib.charger.handler;

import de.uni_passau.fim.bochenek.ma.lib.charger.interfaces.IHandler;

/**
 * 
 * @author Martin Bochenek
 *
 */
public class MessageHandler implements IHandler {

	public void callback() {
		// TODO Auto-generated method stub
	}

	public void callback(String msg) {
		System.out.println("MessageHandler received: " + msg);
	}

}
