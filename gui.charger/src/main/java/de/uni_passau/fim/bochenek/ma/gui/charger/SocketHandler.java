package de.uni_passau.fim.bochenek.ma.gui.charger;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * 
 * @author Martin Bochenek
 *
 */
public class SocketHandler extends WebSocketHandler {

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(AppSocket.class);
	}

}
