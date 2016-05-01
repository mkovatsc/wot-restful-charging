package de.uni_passau.fim.bochenek.ma.gui.charger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * 
 * @author Martin Bochenek
 *
 */
public class SocketHandler extends WebSocketHandler {

	// Singleton
	private static SocketHandler instance;

	private static List<Session> listeners;

	private SocketHandler() {

	}

	public static synchronized SocketHandler getInstance() {
		if (SocketHandler.instance == null) {
			instance = new SocketHandler();
			listeners = new LinkedList<Session>();
			
			Timer timer = new Timer();
			timer.schedule(new WebsocketKeepalive(), 0, 10000);
		}
		return SocketHandler.instance;
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(AppSocket.class);
	}

	public void addListener(Session listener) {
		listeners.add(listener);
	}

	public void pushToListeners(String message) {
		for (Session listener : listeners) {
			try {
				listener.getRemote().sendString(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @author Martin Bochenek
	 *
	 */
	static class WebsocketKeepalive extends TimerTask {

		@Override
		public void run() {
			instance.pushToListeners("Ping!");
		}

	}

}
