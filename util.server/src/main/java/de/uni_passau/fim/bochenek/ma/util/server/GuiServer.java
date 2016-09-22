package de.uni_passau.fim.bochenek.ma.util.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;

import com.google.gson.JsonObject;

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
		if (appServer != null && !appServer.isStarted()) {
			appServer.start();
		}
		if (socketServer != null && !socketServer.isStarted()) {
			socketServer.start();
		}
	}

	public void stop() throws Exception {
		if (appServer != null && appServer.isStarted()) {
			appServer.stop();
		}
		if (socketServer != null && socketServer.isStarted()) {
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
		ResourceHandler resourceHandler = new ResourceHandler();
		if (url != null) {
			resourceHandler.setResourceBase(url.toExternalForm());
		}

		// Create contexts for the application and the configuration
		ContextHandler ctxApp = new ContextHandler("/");
		ctxApp.setHandler(resourceHandler);
		ContextHandler ctxConfig = new ContextHandler("/config");
		ctxConfig.setHandler(new ConfigHandler());

		// Add the contexts to the application server
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[]{ctxApp, ctxConfig});
		appServer.setHandler(contexts);
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

	public boolean isRunning() {
		return appServer.isRunning() && socketServer.isRunning();
	}

	private class ConfigHandler extends AbstractHandler {

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			response.setContentType("application/json; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);

			// Create the configuration object
			JsonObject config = new JsonObject();
			config.addProperty("socketPort", socketPort);
			config.addProperty("carModel", "bmw_i3"); // TODO Get from command line arguments

			PrintWriter out = response.getWriter();
			out.println(config.toString());

			baseRequest.setHandled(true);
		}

	}

}
