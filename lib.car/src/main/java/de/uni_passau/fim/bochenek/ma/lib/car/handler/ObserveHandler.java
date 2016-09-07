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
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoad(CoapResponse res) {
		car.sendToCar(String.format(TMPL_NOTIFY, res.getResponseText()));
	}

}
