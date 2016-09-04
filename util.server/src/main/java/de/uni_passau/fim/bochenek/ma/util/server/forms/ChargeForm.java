package de.uni_passau.fim.bochenek.ma.util.server.forms;

import com.google.gson.JsonObject;

import ch.ethz.inf.vs.hypermedia.client.MediaType;

@MediaType(contentType = 65012, mediaType = "application/charge+json")
public class ChargeForm extends BaseForm {

	private int soc;
	private double targetCurrent;

	public ChargeForm() {

	}

	public ChargeForm(JsonObject jo) {
		this.parse(jo);
	}

	public int getSoc() {
		return soc;
	}

	public void setSoc(int soc) {
		this.soc = soc;
	}

	public double getTargetCurrent() {
		return targetCurrent;
	}

	public void setTargetCurrent(double targetCurrent) {
		this.targetCurrent = targetCurrent;
	}

}
