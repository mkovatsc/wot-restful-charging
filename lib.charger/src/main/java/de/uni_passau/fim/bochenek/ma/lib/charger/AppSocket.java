package de.uni_passau.fim.bochenek.ma.lib.charger;

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

import de.uni_passau.fim.bochenek.ma.lib.charger.messages.ActionMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

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
		SocketHandler.getInstance().addListener(session);
	}

	@OnWebSocketMessage
	public void onMessage(String message) {

		// TODO Copied from lib.car -> find a better solution!
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
					SocketHandler socket = SocketHandler.getInstance();
					ChargerData chargerData = socket.getChargerData();

					switch (type) {
						case EVENT : // TODO
							break;
						case ACTION :
							ActionMessage actMsg = gson.fromJson(msg.getAsJsonObject().get("data"), ActionMessage.class);

							// DEBUG
							logger.log(Level.INFO, "Action received: {0}", new Object[]{actMsg.getAction()});

							// Handle triggered action
							switch (actMsg.getAction()) {
								case "updateCableCheckStatus" :
									chargerData.setCableCheckStatus(actMsg.getCableCheckStatus());

									// DEBUG
									socket.pushToListeners(MessageType.DEBUG, new Message("Charger updated cableCheckStatus to: " + chargerData.getCableCheckStatus()));
									break;
								case "updatePresentVoltage" :
									chargerData.setPresentVoltage(actMsg.getPresentVoltage());

									// DEBUG
									socket.pushToListeners(MessageType.DEBUG, new Message("Charger updated presentVoltage to: " + chargerData.getPresentVoltage()));
									break;
								default :
									// TODO
							}
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
