package de.uni_passau.fim.bochenek.ma.gui.car;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.uni_passau.fim.bochenek.ma.gui.car.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.car.Car;
import de.uni_passau.fim.bochenek.ma.lib.car.messages.ActionMessage;
import de.uni_passau.fim.bochenek.ma.lib.car.messages.EventMessage;
import de.uni_passau.fim.bochenek.ma.lib.car.messages.Message.MessageType;

@WebSocket
public class AppSocket {

	private Logger logger = Logger.getLogger(AppSocket.class.getName());;

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		logger.log(Level.INFO, "Connection closed: " + reason);
		SocketHandler.getInstance().cleanListeners();
	}

	@OnWebSocketError
	public void onError(Throwable t) {
		logger.log(Level.INFO, "Error: " + t.getMessage());
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		logger.log(Level.INFO, "Connection from: " + session.getRemoteAddress().getAddress());

		// Automatically add client to listeners on connect
		Car car = SocketHandler.getInstance().addListener(session);
		String register = "{\"type\" : \"REGISTER\", \"data\" : {\"uuid\" : \"%s\"}}";
		car.sendToCar(String.format(register, car.getUuid().toString()));

		// TODO Wait some time to give car the chance to present its UUID
	}

	@OnWebSocketMessage
	public void onMessage(Session session, String message) {
		
		// DEBUG
		logger.log(Level.INFO, "Message received: " + message);

		if (message != null && !message.equals("")) {
			JsonParser parser = new JsonParser();
			JsonElement msg = null;
			try {
				msg = parser.parse(message);
			} catch (JsonSyntaxException jse) {
				// TODO Something to do here?
				logger.log(Level.WARNING, "No valid JSON received. (" + message + ")");
			}

			if (msg != null && msg.isJsonObject() && msg.getAsJsonObject().get("type") != null) {
				try {
					MessageType type = MessageType.valueOf(msg.getAsJsonObject().get("type").getAsString());
					Gson gson = new Gson();
					Car car;
					switch (type) {
						case EVENT :
							EventMessage evtMsg = gson.fromJson(msg.getAsJsonObject().get("data"), EventMessage.class);

							// DEBUG
							logger.log(Level.INFO, "Car plugged in: {0}", new Object[]{evtMsg.isPluggedIn()});

							car = SocketHandler.getInstance().getCarFor(session);
							String plugStatus = evtMsg.isPluggedIn() ? "Car (%s) plugged in." : "Car (%s) unplugged.";
							String debug = "{\"type\" : \"DEBUG\", \"data\" : {\"message\" : \"%s\"}}";
							car.sendToCharger(String.format(debug, String.format(plugStatus, car.getUuid().toString())));

							break;
						case ACTION : // TODO Only debugging right now
							ActionMessage actMsg = gson.fromJson(msg.getAsJsonObject().get("data"), ActionMessage.class);

							// DEBUG
							logger.log(Level.INFO, "Action received: {0}", new Object[]{actMsg.getNotify()});


							break;
						case KEEPALIVE :

							// DEBUG
							logger.log(Level.INFO, "Answer to keepalive received.");
							break;
						default :
							logger.log(Level.INFO, "No handler found for this type of message. (" + message + ")");
							break;
					}
					// TODO Proper handling for invalid contents
				} catch (IllegalArgumentException iae) {
					// TODO Find some elegant solution
					logger.log(Level.WARNING, "No valid message type. (" + message + ")");
				}
			}
		}
	}

}
