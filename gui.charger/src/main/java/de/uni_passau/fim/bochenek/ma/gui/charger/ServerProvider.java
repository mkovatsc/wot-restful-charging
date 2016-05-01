package de.uni_passau.fim.bochenek.ma.gui.charger;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_passau.fim.bochenek.ma.lib.charger.Charger;
import de.uni_passau.fim.bochenek.ma.lib.charger.CustomDeliverer;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.MessageHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.resources.TestResource;
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
		CustomDeliverer deliverer = new CustomDeliverer(charger.getRoot());
		deliverer.registerHandler("default", handler);
		charger.setMessageDeliverer(deliverer);
		charger.add(new TestResource("iamyourcharger"));
		charger.start();

		// Debugging information
		logger.log(Level.INFO, "GuiServer (Application) started on: " + server.getAppPort());
		logger.log(Level.INFO, "GuiServer (Socket) started on: " + server.getSocketPort());
	}

}
