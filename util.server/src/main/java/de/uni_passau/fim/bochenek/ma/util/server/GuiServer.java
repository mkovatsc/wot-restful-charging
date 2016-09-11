package de.uni_passau.fim.bochenek.ma.util.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;

public class GuiServer {

	private int appPort;
	private int socketPort;
	private Server appServer;
	private Server socketServer;

	public GuiServer(int appPort, URL url, int socketPort, WebSocketHandler socketHandler) {
		setupAppServer(appPort, url);
		setupSocketServer(socketPort, socketHandler); // TODO SSL?
	}

	public GuiServer(int appPort, URL url) {
		setupAppServer(appPort, url);
	}

	public void start() throws Exception {
		if (appServer != null) {
			appServer.start();
		}
		if (socketServer != null) {
			socketServer.start();
		}
	}

	public void stop() throws Exception {
		if (appServer != null && appServer.isStarted()) {
			appServer.stop();
		}
		if (socketServer != null && appServer.isStarted()) {
			socketServer.stop();
		}
	}

	private boolean isPortAvailable(int port) {
		try {
			ServerSocket srv = new ServerSocket(port);
			srv.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private void setupAppServer(int appPort, URL url) {

		// Randomly allocate port, if selected one is already taken
		this.appPort = isPortAvailable(appPort) ? appPort : 0;

		// Set up the application server
		appServer = new Server(appPort);
		ResourceHandler resource_handler = new ResourceHandler();
		if (url != null) {
			resource_handler.setResourceBase(url.toExternalForm());
		} else {
			// TODO Actual error handling
		}
		appServer.setHandler(resource_handler);
	}

	private void setupSocketServer(int socketPort, WebSocketHandler socketHandler) {

		// Randomly allocate port, if selected one is already taken
		this.socketPort = isPortAvailable(socketPort) ? socketPort : 0;

		// Set up the socket server
		socketServer = new Server(socketPort);
		socketServer.setHandler(socketHandler);
	}

	public int getAppPort() {
		return appPort; // TODO May be uninitialized
	}

	public int getSocketPort() {
		return socketPort; // TODO May be uninitialized
	}

}
