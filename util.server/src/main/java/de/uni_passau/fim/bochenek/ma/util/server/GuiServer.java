package de.uni_passau.fim.bochenek.ma.util.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;

/**
 * TODO
 * 
 * @author Martin Bochenek
 *
 */
public class GuiServer {

	private int appPort;
	private int socketPort;
	private Server appServer;
	private Server socketServer;

	/**
	 * TODO
	 * 
	 * @param appPort
	 * @param url
	 * @param socketPort
	 * @param socketHandler
	 */
	public GuiServer(int appPort, URL url, int socketPort, WebSocketHandler socketHandler) {

		// Randomly allocate port, if selected one is already taken
		this.appPort = isPortAvailable(appPort) ? appPort : 0;
		this.socketPort = isPortAvailable(socketPort) ? socketPort : 0;

		// Set up the application server
		appServer = new Server(appPort);
		ResourceHandler resource_handler = new ResourceHandler();
		if (url != null) {
			resource_handler.setResourceBase(url.toExternalForm());
		} else {
			// TODO Actual error handling
		}
		appServer.setHandler(resource_handler);

		// Set up the socket server
		socketServer = new Server(socketPort);
		socketServer.setHandler(socketHandler);
	}

	/**
	 * TODO
	 */
	public void start() {
		// TODO Better check
		if (appServer != null) {
			try {
				appServer.start();
				socketServer.start(); // TODO No check for socketServer above

				// TODO join() ?
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
		if (appServer != null && appServer.isStarted()) {
			try {
				appServer.stop();
				socketServer.stop(); // TODO No check for socketServer above
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

	public int getAppPort() {
		return appPort; // TODO May be uninitialized
	}

	public int getSocketPort() {
		return socketPort; // TODO May be uninitialized
	}

}
