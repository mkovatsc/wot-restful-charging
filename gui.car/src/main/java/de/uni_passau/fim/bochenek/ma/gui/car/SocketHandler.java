package de.uni_passau.fim.bochenek.ma.gui.car;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.google.gson.Gson;

import de.uni_passau.fim.bochenek.ma.gui.car.AppSocket;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;

/**
 * TODO
 * 
 * @author Martin Bochenek
 *
 */
public class SocketHandler extends WebSocketHandler {

	// Singleton
	private static SocketHandler instance;

	private static List<Session> listeners;

	// Config
	private static final int KEEPALIVE_INTERVAL = 15; // Seconds
	private static final String KEEPALIVE_MESSAGE = "Ping!";
	private static final String MSG_CONTAINER = "{\"type\":\"%s\",\"content\":%s}";

	private SocketHandler() {

	}

	public static synchronized SocketHandler getInstance() {
		if (SocketHandler.instance == null) {
			instance = new SocketHandler();
			listeners = new LinkedList<Session>();

			// Start keep-alive timer
			Timer timer = new Timer();
			timer.schedule(new WebsocketKeepalive(), 0, KEEPALIVE_INTERVAL * 1000);
		}
		return SocketHandler.instance;
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(AppSocket.class);
	}

	/**
	 * TODO
	 * 
	 * @param listener
	 */
	public void addListener(Session listener) {
		listeners.add(listener);
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public boolean cleanListeners() {
		return listeners.removeIf(s -> !s.isOpen());
	}

	/**
	 * TODO
	 * 
	 * @param message
	 */
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
	 * @param message
	 */
	public void pushToListeners(MessageType type, Message message) {
		Gson gson = new Gson();
		this.pushToListeners(String.format(MSG_CONTAINER, type, gson.toJson(message)));
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
			instance.pushToListeners(KEEPALIVE_MESSAGE);
		}

	}

}
