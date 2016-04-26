package de.uni_passau.fim.bochenek.ma.gui.charger;

import de.uni_passau.fim.bochenek.ma.util.server.GuiServer;

/**
 * TODO
 * 
 * @author Martin Bochenek
 *
 */
public class ServerProvider {

	public static void main(String[] args) {
		GuiServer server = new GuiServer(8080, ServerProvider.class.getResource("/webapp"));
		server.start();
	}

}
