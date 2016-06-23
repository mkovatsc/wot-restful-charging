package de.uni_passau.fim.bochenek.ma.gui.charger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_passau.fim.bochenek.ma.gui.charger.handler.MessageHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.Charger;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage.EvStatus;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage.SeStatus;
import de.uni_passau.fim.bochenek.ma.util.server.GuiServer;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

/**
 * TODO
 * 
 * @author Martin Bochenek
 *
 */
public class ServerProvider {

	private static Logger logger = Logger.getLogger(ServerProvider.class.getName());;

	public static void main(String[] args) {

		// Config
		int appPort = 8080;
		int socketPort = 8081;
		URL appUrl = ServerProvider.class.getResource("/webapp");

		// Start GUI server
		GuiServer server = new GuiServer(appPort, appUrl, socketPort, SocketHandler.getInstance());
		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Prepare data POJOs for charger and connected cars
		ChargerData chargerData = new ChargerData();
		Map<UUID, CarData> carData = new HashMap<UUID, CarData>();

		// Setup and start charger
		Charger charger = new Charger(chargerData, carData);
		MessageHandler handler = new MessageHandler();
		charger.registerHandler(MessageType.DEBUG, handler);
		charger.registerHandler(MessageType.STATUS, handler);
		charger.start();

		// Debugging information
		logger.log(Level.INFO, "GuiServer (Application) started on: " + server.getAppPort());
		logger.log(Level.INFO, "GuiServer (Socket) started on: " + server.getSocketPort());

		// Start regular interface update
		Timer timer = new Timer();
		timer.schedule(new InterfaceUpdate(chargerData, carData), 0, 1000);

		// TODO maybe use POJOs for UI update
	}

	/**
	 * TODO doing it this way is so f***ing wrong...
	 * 
	 * @author Martin Bochenek
	 *
	 */
	static class InterfaceUpdate extends TimerTask {

		private ChargerData charger;
		private Map<UUID, CarData> cars;

		public InterfaceUpdate(ChargerData charger, Map<UUID, CarData> cars) {
			this.charger = charger;
			this.cars = cars;
		}

		@Override
		public void run() {
			StatusMessage status = new StatusMessage();
			EvStatus ev = status.getEvStatus();
			SeStatus se = status.getSeStatus();

			// TODO what if there is more than just one EV connected?
			for (CarData car : cars.values()) {
				ev.setStateOfCharge(car.getSoc());
				ev.setMaximumVoltage(car.getMaxVoltage());
				ev.setMaximumCurrent(car.getMaxCurrent());
				ev.setTargetVoltage(car.getTargetVoltage());
				ev.setTargetCurrent(car.getTargetCurrent());
			}

			se.setPresentVoltage(charger.getPresentVoltage());
			se.setPresentCurrent(charger.getPresentCurrent());
			
			// TODO Only update UI if any car is plugged in
			if (cars.size() > 0) {
				SocketHandler.getInstance().pushToListeners(MessageType.STATUS, status);
			}
		}

	}

}
