package de.uni_passau.fim.bochenek.ma.lib.charger;

import java.util.List;

import de.uni_passau.fim.bochenek.ma.lib.charger.handler.MessageHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.interfaces.IHandler;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		IHandler handler = new MessageHandler();
		Charger charger = new Charger();
		charger.registerHandler(handler);

		List<String> tmp2 = charger.getRegisteredHandlers();

		System.out.println("Registered handlers: " + tmp2.size());
		charger.doSomething("Hello, World!");
	}

}
