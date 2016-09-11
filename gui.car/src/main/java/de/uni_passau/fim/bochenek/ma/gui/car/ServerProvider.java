package de.uni_passau.fim.bochenek.ma.gui.car;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_passau.fim.bochenek.ma.lib.car.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.util.server.GuiServer;

public class ServerProvider {

	// Configuration
	private static final int appPort = 8090;
	private static final int socketPort = 8091;
	private static final URL appUrl = ServerProvider.class.getResource("/webapp");

	private static Logger logger = Logger.getLogger(ServerProvider.class.getName());

	public static void main(String[] args) {

		// Start GUI server
		GuiServer server = new GuiServer(appPort, appUrl, socketPort, SocketHandler.getInstance());
		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Debugging information
		logger.log(Level.INFO, "GuiServer (Application) started on: " + server.getAppPort());
		logger.log(Level.INFO, "GuiServer (Socket) started on: " + server.getSocketPort());
	}

}
