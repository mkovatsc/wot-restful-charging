package de.uni_passau.fim.bochenek.ma.gui.charger;

import java.net.URL;

import de.uni_passau.fim.bochenek.ma.util.server.GuiServer;

public class Server {

	public static void main(String[] args) {
		URL baseUrl = Server.class.getResource("/webapp");
		String basePath = baseUrl.toExternalForm();

		GuiServer server = new GuiServer(8080, basePath);
		server.start();
	}

}
