package de.uni_passau.fim.bochenek.ma.gui.charger.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_passau.fim.bochenek.ma.gui.charger.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.IHandler;

public class MessageHandler implements IHandler {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public void callback() {
		// TODO Auto-generated method stub

	}

	public void callback(String msg) {
		SocketHandler.getInstance().pushToListeners(msg);
		logger.log(Level.INFO, "{0} received callback: {1}", new Object[]{this.getClass().getSimpleName(), msg});
	}

}
