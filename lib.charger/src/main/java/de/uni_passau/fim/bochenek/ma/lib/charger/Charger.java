package de.uni_passau.fim.bochenek.ma.lib.charger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.uni_passau.fim.bochenek.ma.lib.charger.interfaces.IHandler;

/**
 * 
 * @author Martin Bochenek
 *
 */
public class Charger {

	private Map<String, IHandler> handlers;

	public Charger() {
		handlers = new HashMap<String, IHandler>();
	}

	/**
	 * 
	 * @param handler
	 */
	public void registerHandler(IHandler handler) {
		handlers.put(handler.getClass().getSimpleName(), handler);
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getRegisteredHandlers() {
		return new ArrayList<String>(handlers.keySet());
	}

	public void doSomething(String msg) {
		try {
			Thread.sleep(2000);
			handlers.forEach((key, handler) -> handler.callback(msg));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
