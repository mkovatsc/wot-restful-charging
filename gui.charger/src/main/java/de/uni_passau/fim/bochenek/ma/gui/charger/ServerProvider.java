package de.uni_passau.fim.bochenek.ma.gui.charger;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.server.resources.Resource;

import de.uni_passau.fim.bochenek.ma.gui.charger.handler.MessageHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.Charger;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage.EvStatus;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage.SeStatus;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev.EvChargingComplete;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev.EvMaxValues;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev.EvReadyToCharge;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev.EvSoc;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev.EvTargetValues;
import de.uni_passau.fim.bochenek.ma.util.server.GuiServer;

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

		// Setup and start charger
		Charger charger = new Charger();
		MessageHandler handler = new MessageHandler();
		charger.registerHandler(MessageType.DEBUG, handler);
		charger.registerHandler(MessageType.STATUS, handler);
		charger.start();

		// Debugging information
		logger.log(Level.INFO, "GuiServer (Application) started on: " + server.getAppPort());
		logger.log(Level.INFO, "GuiServer (Socket) started on: " + server.getSocketPort());

		// Start regular interface update
		Timer timer = new Timer();
		timer.schedule(new InterfaceUpdate(charger.getRoot()), 0, 1000); // TODO find a better solution!
	}

	/**
	 * TODO doing it this way is so f***ing wrong...
	 * 
	 * @author Martin Bochenek
	 *
	 */
	static class InterfaceUpdate extends TimerTask {

		private Resource root;

		public InterfaceUpdate(Resource root) {
			this.root = root;
		}

		@Override
		public void run() {
			StatusMessage status = new StatusMessage();
			EvStatus ev = status.getEvStatus();
			SeStatus se = status.getSeStatus();

			// TODO what if there is more than just one EV connected?
			for (Resource res1 : root.getChild("ev").getChildren()) {
				for (Resource res2 : res1.getChildren()) {
					switch (res2.getName()) {
						case "stateOfCharge" :
							ev.setStateOfCharge(((EvSoc) res2).getStateOfCharge());
							break;
						case "chargingComplete" :
							ev.setChargingComplete(((EvChargingComplete) res2).isChargingComplete());
							break;
						case "readyToCharge" :
							ev.setReadyToCharge(((EvReadyToCharge) res2).isReadyToCharge());
							break;
						case "maxValues" :
							ev.setMaximumVoltage(((EvMaxValues) res2).getVoltage());
							ev.setMaximumCurrent(((EvMaxValues) res2).getCurrent());
							break;
						case "targetValues" :
							ev.setTargetVoltage(((EvTargetValues) res2).getVoltage());
							ev.setTargetCurrent(((EvTargetValues) res2).getCurrent());
							break;
					}
				}
			}

			SocketHandler.getInstance().pushToListeners(MessageType.STATUS, status);
		}

	}

}
