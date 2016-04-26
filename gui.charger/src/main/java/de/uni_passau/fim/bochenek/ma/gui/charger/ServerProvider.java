package de.uni_passau.fim.bochenek.ma.gui.charger;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		SocketHandler sockHandler = new SocketHandler();

		// Start GUI server
		GuiServer server = new GuiServer(appPort, appUrl, socketPort, sockHandler);
		server.start();

		// Debugging information
		logger.log(Level.INFO, "GuiServer (Application) started on: " + server.getAppPort());
		logger.log(Level.INFO, "GuiServer (Socket) started on: " + server.getSocketPort());
	}

}
