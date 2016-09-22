package de.uni_passau.fim.bochenek.ma.gui.charger;

import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import de.uni_passau.fim.bochenek.ma.lib.charger.Charger;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage.EvStatus;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage.SeStatus;
import de.uni_passau.fim.bochenek.ma.util.server.GuiServer;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

public class ServerProvider {

	// Configuration
	private static final int DEFAULT_PORT_APP = 8080;
	private static final int DEFAULT_PORT_SOCKET = 8081;
	private static final URL APP_URL = ServerProvider.class.getResource("/webapp");

	private static Logger logger = Logger.getLogger(ServerProvider.class.getName());;

	public static void main(String[] args) {

		// Prepare for command line parsing
		CommandLineParser parser = new DefaultParser();

		// Create available options
		Options options = new Options();
		options.addOption(Option.builder("a").longOpt("app-port").desc("The port where the UI will be served.").hasArg().type(Integer.class).build());
		options.addOption(Option.builder("s").longOpt("socket-port").desc("The port where the websocket will be listening.").hasArg().type(Integer.class).build());

		// Try to parse the arguments and print help if something went wrong
		try {
			CommandLine line = parser.parse(options, args);

			int portApp = (line.getParsedOptionValue("a") != null) ? (Integer) line.getParsedOptionValue("a") : DEFAULT_PORT_APP;
			int portSocket = (line.getParsedOptionValue("s") != null) ? (Integer) line.getParsedOptionValue("s") : DEFAULT_PORT_SOCKET;

			// Start GUI server
			GuiServer server = new GuiServer(portApp, APP_URL, portSocket, SocketHandler.getInstance());
			try {
				server.start();
			} catch (Exception e) {
				logger.log(Level.INFO, "GuiServer could not be successfully started.");
			}

			// Debugging information
			if (server.isRunning()) {
				logger.log(Level.INFO, "GuiServer (Application) started on: " + server.getAppPort());
				logger.log(Level.INFO, "GuiServer (Socket) started on: " + server.getSocketPort());
			}

			// Prepare data POJOs for charger and connected cars
			ChargerData chargerData = new ChargerData();
			SocketHandler.getInstance().setChargerData(chargerData); // TODO Part of an ugly hack...

			// Setup and start charger
			Charger charger = new Charger(chargerData);
			charger.start();

			// Start regular interface update
			Timer timer = new Timer();
			timer.schedule(new InterfaceUpdate(chargerData), 0, 1000);
		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			StringBuilder header = new StringBuilder("Error: ");

			if (exp instanceof MissingOptionException) {
				header.append("You forgot to specify a required option. ");
			}
			if (exp instanceof MissingArgumentException) {
				header.append("A provided option is missing an argument. ");
			}
			if (exp instanceof UnrecognizedOptionException) {
				header.append("A provided option could not be recognized.");
			}

			formatter.printHelp("charger", header.toString(), options, "");
		}
	}

	static class InterfaceUpdate extends TimerTask {

		private ChargerData charger;

		public InterfaceUpdate(ChargerData charger) {
			this.charger = charger;
		}

		@Override
		public void run() {
			StatusMessage status = new StatusMessage();
			EvStatus ev = status.getEvStatus();
			SeStatus se = status.getSeStatus();

			// TODO what if there is more than just one EV connected?
			for (Map.Entry<UUID, CarData> entry : charger.getCars()) {
				CarData car = entry.getValue();
				ev.setUuid(entry.getKey());
				ev.setStateOfCharge(car.getSoc());
				ev.setMaximumVoltage(car.getMaxVoltage());
				ev.setMaximumCurrent(car.getMaxCurrent());
				ev.setTargetVoltage(car.getTargetVoltage());
				ev.setTargetCurrent(car.getTargetCurrent());
			}

			se.setPresentVoltage(charger.getPresentVoltage());
			se.setPresentCurrent(charger.getPresentCurrent());

			// Only update UI if at least one car is plugged in
			if (charger.connectedCars() > 0) {
				SocketHandler.getInstance().pushToListeners(MessageType.STATUS, status);
			}
		}

	}

}
