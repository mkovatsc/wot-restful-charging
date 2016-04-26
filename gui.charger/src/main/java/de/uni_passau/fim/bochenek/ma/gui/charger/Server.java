package de.uni_passau.fim.bochenek.ma.gui.charger;

import de.uni_passau.fim.bochenek.ma.util.server.GuiServer;

public class Server {

	public static void main(String[] args) {
		GuiServer server = new GuiServer(8080, Server.class.getResource("/webapp"));
		server.start();
	}

}
