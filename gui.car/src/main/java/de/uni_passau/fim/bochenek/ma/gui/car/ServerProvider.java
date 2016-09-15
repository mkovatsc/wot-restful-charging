package de.uni_passau.fim.bochenek.ma.gui.car;

import java.net.URL;
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

import de.uni_passau.fim.bochenek.ma.lib.car.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.util.server.GuiServer;

public class ServerProvider {

	// Configuration
	private static final int DEFAULT_PORT_APP = 8090;
	private static final int DEFAULT_PORT_SOCKET = 8091;
	private static final URL APP_URL = ServerProvider.class.getResource("/webapp");

	private static Logger logger = Logger.getLogger(ServerProvider.class.getName());

	public static void main(String[] args) {

		// Prepare for command line parsing
		CommandLineParser parser = new DefaultParser();

		// Create available options
		Options options = new Options();
		options.addOption(Option.builder("a")
				.longOpt("app-port")
				.desc("The port where the UI will be served.")
				.hasArg()
				.type(Integer.class)
				.build());
		options.addOption(Option.builder("s")
				.longOpt("socket-port")
				.desc("The port where the websocket will be listening.")
				.hasArg()
				.type(Integer.class)
				.build());

		// Try to parse the arguments and print help if something wents wrong
		try {
			CommandLine line = parser.parse(options, args);

			int portApp = (line.getParsedOptionValue("a") != null) ? (Integer) line.getParsedOptionValue("a") : DEFAULT_PORT_APP;
			int portSocket = (line.getParsedOptionValue("s") != null) ? (Integer) line.getParsedOptionValue("s") : DEFAULT_PORT_SOCKET;

			// Start GUI server
			GuiServer server = new GuiServer(portApp, APP_URL, portSocket, SocketHandler.getInstance());
			try {
				server.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Debugging information
			logger.log(Level.INFO, "GuiServer (Application) started on: " + server.getAppPort());
			logger.log(Level.INFO, "GuiServer (Socket) started on: " + server.getSocketPort());
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
			
			formatter.printHelp("car", header.toString(), options, "");
		}
	}

}
