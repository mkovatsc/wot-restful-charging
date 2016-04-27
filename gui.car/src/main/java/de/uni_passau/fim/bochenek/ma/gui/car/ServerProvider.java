package de.uni_passau.fim.bochenek.ma.gui.car;

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
		int appPort = 8090;
		URL appUrl = ServerProvider.class.getResource("/webapp");

		// Start GUI server
		GuiServer server = new GuiServer(appPort, appUrl);
		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Debugging information
		logger.log(Level.INFO, "GuiServer (Application) started on: " + server.getAppPort());
	}

}
