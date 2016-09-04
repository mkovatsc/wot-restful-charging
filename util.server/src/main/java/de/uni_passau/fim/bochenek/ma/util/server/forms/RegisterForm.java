package de.uni_passau.fim.bochenek.ma.util.server.forms;

import com.google.gson.JsonObject;

import ch.ethz.inf.vs.hypermedia.client.MediaType;
import de.uni_passau.fim.bochenek.ma.util.server.enums.ChargingType;

@MediaType(contentType = 65010, mediaType = "application/register+json")
public class RegisterForm extends BaseForm {

	private int soc;
	private double maxVoltage;
	private double maxCurrent;
	private ChargingType chargingType;

	public RegisterForm() {

	}

	public RegisterForm(JsonObject jo) {
		this.parse(jo);
	}

	public int getSoc() {
		return soc;
	}

	public void setSoc(int soc) {
		this.soc = soc;
	}

	public double getMaxVoltage() {
		return maxVoltage;
	}

	public void setMaxVoltage(double maxVoltage) {
		this.maxVoltage = maxVoltage;
	}

	public double getMaxCurrent() {
		return maxCurrent;
	}

	public void setMaxCurrent(double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}

	public ChargingType getChargingType() {
		return chargingType;
	}

	public void setChargingType(ChargingType chargingType) {
		this.chargingType = chargingType;
	}

}
