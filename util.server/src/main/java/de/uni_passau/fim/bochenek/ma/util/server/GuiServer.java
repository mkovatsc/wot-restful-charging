package de.uni_passau.fim.bochenek.ma.util.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

/**
 * TODO
 * 
 * @author Martin Bochenek
 *
 */
public class GuiServer {

	private int port;
	private Server server;

	/**
	 * TODO
	 * 
	 * @param port
	 * @param url
	 */
	public GuiServer(int port, URL url) {
		// TODO
		if (port > 1024 && port <= 65535 && isPortAvailable(port)) {
			this.port = port;
		} else {
			int tmpPort = (int) Math.floor(Math.random() * 64511) + 1024;
			while (!isPortAvailable(tmpPort)) {
				tmpPort = (int) Math.floor(Math.random() * 64511) + 1024;
			}
			this.port = tmpPort;
		}

		server = new Server(port);

		ResourceHandler resource_handler = new ResourceHandler();

		if (url != null) {
			resource_handler.setResourceBase(url.toExternalForm());
		} else {
			// TODO Actual error handling
		}

		GzipHandler gzip = new GzipHandler();
		server.setHandler(gzip);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
		gzip.setHandler(handlers);
	}

	/**
	 * TODO
	 */
	public void start() {
		// TODO Better check
		if (server != null) {
			try {
				server.start();
				server.join();
			} catch (Exception e) {
				// TODO Correct handling or simply throw
				e.printStackTrace();
			}
		}
	}

	/**
	 * TODO
	 */
	public void stop() {
		if (server != null && server.isStarted()) {
			try {
				server.stop();
			} catch (Exception e) {
				// TODO Correct handling or simply throw
				e.printStackTrace();
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @param port
	 * @return
	 */
	private boolean isPortAvailable(int port) {
		try {
			ServerSocket srv = new ServerSocket(port);
			srv.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public int getPort() {
		return this.port;
	}

}
