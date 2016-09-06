package de.uni_passau.fim.bochenek.ma.lib.car;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import de.uni_passau.fim.bochenek.ma.lib.car.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.car.messages.ActionMessage;
import de.uni_passau.fim.bochenek.ma.lib.car.messages.EventMessage;
import de.uni_passau.fim.bochenek.ma.lib.car.messages.Message.MessageType;

@WebSocket
public class AppSocket {

	// Message templates
	private static final String TMPL_ANSWER = "{\"type\" : \"%s\", \"data\" : %s }";

	private Logger logger = Logger.getLogger(AppSocket.class.getName());

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
	public void onMessage(Session session, String message) {

		if (message != null && !message.equals("")) {
			JsonParser parser = new JsonParser();
			JsonElement msg = null;
			try {
				msg = parser.parse(message);
			} catch (JsonSyntaxException jse) {
				// TODO Something to do here?
				logger.log(Level.WARNING, "No valid JSON received. (" + message + ")");
				return;
			}

			if (msg != null && msg.isJsonObject() && msg.getAsJsonObject().get("type") != null) {
				try {
					MessageType type = MessageType.valueOf(msg.getAsJsonObject().get("type").getAsString());
					Gson gson = new Gson();
					Car car = SocketHandler.getInstance().getCarFor(session);

					switch (type) {
						case EVENT :
							EventMessage evtMsg = gson.fromJson(msg.getAsJsonObject().get("data"), EventMessage.class);

							if (evtMsg.isPluggedIn()) {
								car.sendToCar(String.format(TMPL_ANSWER, "LINKS", serialize(car.plugIn())));

								// DEBUG
								logger.log(Level.INFO, "Car plugged in.");
							} else {
								car.unplug();

								// DEBUG
								logger.log(Level.INFO, "Car was unplugged.");
							}

							break;
						case ACTION :
							ActionMessage actMsg = gson.fromJson(msg.getAsJsonObject().get("data"), ActionMessage.class);

							// DEBUG
							logger.log(Level.INFO, "Action received: {0}", new Object[]{actMsg.getAction()});

							// Handle triggered action
							switch (actMsg.getAction()) {
								case "follow" :
									CoREHalBase halRes1 = car.follow(actMsg.getHref());
									car.sendToCar(String.format(TMPL_ANSWER, "LINKS", halRes1.json().get("_links")));
									car.sendToCar(String.format(TMPL_ANSWER, "FORMS", halRes1.json().get("_forms")));
									break;
								case "submitForm" :

									// TODO Don't send data that doesn't match media type!
									JsonObject json = new JsonObject();
									json.addProperty("soc", actMsg.getSoc());
									json.addProperty("maxVoltage", actMsg.getMaxVoltage());
									json.addProperty("maxCurrent", actMsg.getMaxCurrent());
									json.addProperty("chargingType", actMsg.getChargingType());
									json.addProperty("targetVoltage", actMsg.getTargetVoltage());
									json.addProperty("targetCurrent", actMsg.getTargetCurrent());
									CoapResponse res = car.sendForm(actMsg.getHref(), actMsg.getMethod(), json);

									if (res != null && res.getOptions().getLocationPathCount() > 0) {
										car.sendToCar(String.format(TMPL_ANSWER, "REDIRECT", "\"" + res.getOptions().getLocationString() + "\""));
									} else {
										CoREHalBase halRes2 = car.getCoREHal();
										car.sendToCar(String.format(TMPL_ANSWER, "LINKS", halRes2.json().get("_links")));
										car.sendToCar(String.format(TMPL_ANSWER, "FORMS", halRes2.json().get("_forms")));
									}

									break;
								default :
									// TODO
									break;
							}
							break;
						case KEEPALIVE :
							logger.log(Level.INFO, "Answer to keepalive received.");
							break;
						default :
							logger.log(Level.WARNING, "No handler found for this type of message. (" + message + ")");
							break;
					}
				} catch (IllegalArgumentException iae) {
					// TODO Find some elegant solution
					logger.log(Level.WARNING, "No valid message type. (" + message + ")"); // TODO Message covers not all the errors!
				}
			}
		} else {
			logger.log(Level.WARNING, "Empty message arrived at socket.");
		}
	}

	private static JsonObject serialize(Set<WebLink> webLinks) {
		JsonObject result = new JsonObject();

		int count = 0; // Enumerate links for the object
		for (WebLink link : webLinks) {
			JsonObject linkObj = new JsonObject();
			linkObj.addProperty("href", link.getURI());
			if (link.getAttributes().getResourceTypes().size() > 0) {
				linkObj.addProperty("rt", link.getAttributes().getResourceTypes().get(0)); // TODO assumes there is only one...
			}
			result.add(Integer.toString(count++), linkObj);
		}

		return result;
	}

}
