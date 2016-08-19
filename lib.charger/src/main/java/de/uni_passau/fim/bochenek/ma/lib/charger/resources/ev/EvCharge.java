package de.uni_passau.fim.bochenek.ma.lib.charger.resources.ev;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.google.gson.Gson;

import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import ch.ethz.inf.vs.hypermedia.corehal.model.Form;
import ch.ethz.inf.vs.hypermedia.corehal.model.Link;
import de.uni_passau.fim.bochenek.ma.lib.charger.handler.SocketHandler;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.EventMessage;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.Message.MessageType;
import de.uni_passau.fim.bochenek.ma.lib.charger.messages.StatusMessage.EvStatus;
import de.uni_passau.fim.bochenek.ma.util.server.data.CarData;
import de.uni_passau.fim.bochenek.ma.util.server.data.ChargerData;

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
		Gson gson = new Gson();
		EvStatus evStatus = gson.fromJson(exchange.getRequestText(), EvStatus.class);
		carData.setTargetVoltage(evStatus.getTargetVoltage());

		EventMessage eMsg = new EventMessage(null);
		eMsg.setTargetVoltage(carData.getTargetVoltage());
		eMsg.setDescription("targetVoltageSet");
		SocketHandler.getInstance().pushToListeners(MessageType.EVENT, eMsg);

		if (this.getChildren().size() < 1) { // TODO
			EvChargingProcess evChargeProc = new EvChargingProcess("process", chargerData, carData);
			EvChargingProcessTC evChargeTC = new EvChargingProcessTC("tC", carData);
			EvChargingProcessTV evChargeTV = new EvChargingProcessTV("tV", carData);
			EvChargingProcessPC evChargePC = new EvChargingProcessPC("pC", chargerData);
			EvChargingProcessPV evChargePV = new EvChargingProcessPV("pV", chargerData);

			evChargeProc.add(evChargeTC);
			evChargeProc.add(evChargeTV);
			evChargeProc.add(evChargePC);
			evChargeProc.add(evChargePV);

			this.add(evChargeProc);
			exchange.setLocationPath(evChargeProc.getURI());
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
			Form voltage = new Form("POST", this.getURI(), "application/json"); // TODO Define application specific format
			hal.addForm("voltage", voltage);
		}

		return hal;
	}

}
