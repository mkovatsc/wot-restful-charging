package de.uni_passau.fim.bochenek.ma.gui.car;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import de.uni_passau.fim.bochenek.ma.gui.car.AppSocket;
import de.uni_passau.fim.bochenek.ma.lib.car.Car;

/**
 * TODO
 * 
 * @author Martin Bochenek
 *
 */
public class SocketHandler extends WebSocketHandler {

	// Singleton
	private static SocketHandler instance;

	private static List<Car> cars;

	// Config
	private static final String CHARGER_URI = "coap://localhost:5683";
	private static final int KEEPALIVE_INTERVAL = 15; // Seconds
	private static final boolean KEEPALIVE_ENABLED = true;
	private static final String KEEPALIVE_MESSAGE = "{\"type\" : \"KEEPALIVE\"}";

	private SocketHandler() {

	}

	public static synchronized SocketHandler getInstance() {
		if (SocketHandler.instance == null) {
			instance = new SocketHandler();
			cars = new LinkedList<Car>();

			// Start keep-alive timer
			if (KEEPALIVE_ENABLED) {
				Timer timer = new Timer();
				timer.schedule(new WebsocketKeepalive(), 0, KEEPALIVE_INTERVAL * 1000);
			}
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
	public Car addListener(Session listener) {
		Car car = new Car(CHARGER_URI);
		car.setSession(listener);
		cars.add(car);
		return car;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public boolean cleanListeners() {
		return cars.removeIf(c -> !c.getSession().isOpen());
	}

	/**
	 * TODO
	 * 
	 * @param session
	 * @return
	 */
	public Car getCarFor(Session session) {
		for (Car car : cars) { // TODO terribly inefficient
			if (car.getSession().hashCode() == session.hashCode()) {
				return car;
			}
		}
		return null;
	}

	/**
	 * TODO
	 * 
	 * @param session
	 * @param message
	 */
	public void pushToCar(Session session, String message) {
		try {
			this.getCarFor(session).getSession().getRemote().sendString(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * TODO
	 * 
	 * @param message
	 */
	private void pushToListeners(String message) {
		for (Car car : cars) {
			try {
				car.getSession().getRemote().sendString(message);
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
			instance.pushToListeners(KEEPALIVE_MESSAGE);
		}

	}

}
