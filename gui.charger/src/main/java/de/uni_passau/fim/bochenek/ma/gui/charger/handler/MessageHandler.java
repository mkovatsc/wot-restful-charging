package de.uni_passau.fim.bochenek.ma.gui.charger.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_passau.fim.bochenek.ma.lib.charger.interfaces.IHandler;

public class MessageHandler implements IHandler {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public void callback() {
		// TODO Auto-generated method stub

	}

	public void callback(String msg) {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, "{0} received callback: {1}", new Object[]{this.getClass().getSimpleName(), msg});
	}

}
