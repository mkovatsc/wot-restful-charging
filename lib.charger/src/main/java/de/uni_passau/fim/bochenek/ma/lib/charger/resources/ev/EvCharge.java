package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import ch.ethz.inf.vs.hypermedia.client.Utils;
import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import ch.ethz.inf.vs.hypermedia.corehal.model.Form;
import ch.ethz.inf.vs.hypermedia.corehal.model.Link;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.EventMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;
import de.uni_passau.fim.bochenek.ma.util.server.forms.ChargeInitForm;

public class EvCharge extends CoapResource {

	private ChargerData chargerData;
	private CarData carData;

	public EvCharge(String name, ChargerData chargerData, CarData carData) {
		super(name);
		this.chargerData = chargerData;
		this.carData = carData;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, this.getRepresentation().toString(), MediaTypeRegistry.APPLICATION_JSON);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		Gson gson = new GsonBuilder().create();
		JsonObject formData = gson.fromJson(exchange.getRequestText(), JsonObject.class); // TODO not very robust...
		ChargeInitForm chargeInit = new ChargeInitForm(formData);
		carData.setTargetVoltage(chargeInit.getTargetVoltage());

		EventMessage eMsg = new EventMessage(null);
		eMsg.setTargetVoltage(carData.getTargetVoltage());
		eMsg.setDescription("targetVoltageSet");
		SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg);

		if (this.getChildren().size() < 1) { // TODO
			EvChargingTask chargingTask = new EvChargingTask("task", chargerData, carData);
			chargingTask.setVisible(false);
			this.add(chargingTask);
			exchange.setLocationPath(chargingTask.getURI());
		}

		exchange.respond(ResponseCode.CREATED, "", MediaTypeRegistry.APPLICATION_JSON); // TODO content?
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	private CoREHalBase getRepresentation() {
		CoREHalBase hal = new CoREHalBase();
		hal.addLink("self", new Link(this.getURI()));

		if (chargerData.getCableCheckStatus() == 2) {
			Form chargeInit = new Form("POST", this.getURI(), Utils.getMediaType(ChargeInitForm.class));
			hal.addForm("init", chargeInit);
		}

		return hal;
	}

}
