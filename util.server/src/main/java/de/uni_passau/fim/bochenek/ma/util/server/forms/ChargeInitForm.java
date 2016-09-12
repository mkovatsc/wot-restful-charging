package de.uni_passau.fim.bochenek.ma.util.server.forms;

import com.google.gson.JsonObject;

import ch.ethz.inf.vs.hypermedia.client.MediaType;

@MediaType(contentType = 65011, mediaType = "application/x.chargeinit+json")
public class ChargeInitForm extends BaseForm {

	private double targetVoltage;

	public ChargeInitForm() {

	}

	public ChargeInitForm(JsonObject jo) {
		this.parse(jo);
	}

	public double getTargetVoltage() {
		return targetVoltage;
	}

	public void setTargetVoltage(double targetVoltage) {
		this.targetVoltage = targetVoltage;
	}

}
