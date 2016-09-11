package de.uni_passau.fim.bochenek.ma.lib.charger.handler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.google.gson.Gson;

import de.uni_passau.fim.bochenek.ma.lib.charger.AppSocket;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

public class SocketHandler extends WebSocketHandler {

	// Singleton
	private static SocketHandler instance;

	private static List<Session> listeners;
	private ChargerData chargerData;

	// Configuration
	private static final int KEEPALIVE_INTERVAL = 15; // Seconds
	private static final boolean KEEPALIVE_ENABLED = true;
	private static final String KEEPALIVE_MESSAGE = "{\"type\" : \"KEEPALIVE\"}";
	private static final String MSG_CONTAINER = "{\"type\":\"%s\",\"data\":%s}";

	private SocketHandler() {

	}

	public static synchronized SocketHandler getInstance() {
		if (SocketHandler.instance == null) {
			instance = new SocketHandler();
			listeners = new LinkedList<Session>();

			// Start keep-alive timer
			if (KEEPALIVE_ENABLED) {
				Timer timer = new Timer();
				timer.schedule(new WebsocketKeepalive(), 0, KEEPALIVE_INTERVAL * 1000);
			}
		}
		return SocketHandler.instance;
	}

	public ChargerData getChargerData() {
		return chargerData;
	}

	public void setChargerData(ChargerData chargerData) {
		this.chargerData = chargerData;
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(AppSocket.class);
	}

	public void addListener(Session listener) {
		listeners.add(listener);
	}

	public boolean cleanListeners() {
		return listeners.removeIf(s -> !s.isOpen());
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

	public void pushToListeners(MessageType type, Message message) {
		Gson gson = new Gson();
		this.pushToListeners(String.format(MSG_CONTAINER, type, gson.toJson(message)));
	}

	static class WebsocketKeepalive extends TimerTask {

		@Override
		public void run() {
			instance.pushToListeners(KEEPALIVE_MESSAGE);
		}

	}

}
