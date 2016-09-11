package de.uni_passau.fim.bochenek.ma.lib.car.handler;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import de.uni_passau.fim.bochenek.ma.lib.car.Car;

public class ObserveHandler implements CoapHandler {

	// Message templates
	private static final String TMPL_NOTIFY = "{\"type\" : \"NOTIFY\", \"data\" : %s }";

	private Car car;

	public ObserveHandler(Car car) {
		this.car = car;
	}

	@Override
	public void onError() {
	}

	@Override
	public void onLoad(CoapResponse res) {
		switch (res.getCode()) {
			case NOT_FOUND :
				// TODO Notify the car, that the observe relation can be deleted?
				break;
			default :
				car.sendToCar(String.format(TMPL_NOTIFY, res.getResponseText()));
		}
	}

}
