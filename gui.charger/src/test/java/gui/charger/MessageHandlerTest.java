package gui.charger;

import static org.junit.Assert.*;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.junit.Test;

/**
 * Not a real test, just a debug routine...
 * 
 * @author Martin Bochenek
 *
 */
public class MessageHandlerTest {

	@Test
	public void test() {

		// Messages to send
		String msg1 = "{\"type\" : \"DEBUG\", \"content\" : {\"message\" : \"This is a test.\"}}";
		String msg2 = "{\"type\" : \"DEBUG\", \"content\" : {\"message\" : \"Another one.\"}}";
		String msg3 = "{\"type\" : \"DEBUG\", \"content\" : {\"message\" : \"And a last one.\"}}";
		String msg4 = "{\"type\" : \"DEBUG\", \"content\" : {\"message\" : \"Repeated message #%d\"}}";
		String status = "{\"type\":\"STATUS\",\"content\":{\"se\":{\"presentVoltage\":%d,\"presentCurrent\":%d,\"currentState\":\"supportedAppProtocol\"},\"ev\":{\"stateOfCharge\":%d,\"maximumVoltageLimit\":400,\"maximumCurrentLimit\":100,\"targetVoltage\":%d,\"targetCurrent\":%d,\"chargingComplete\":%s}}}";

		// Setup CoAP client
		String baseURI = "coap://localhost:5683";
		CoapClient client = new CoapClient();
		client.setURI(baseURI + "/iamyourcharger");

		// Send debug messages
		CoapResponse response = client.post(msg1, MediaTypeRegistry.APPLICATION_JSON);
		assertEquals(ResponseCode.VALID, response.getCode());
		response = client.post(msg2, MediaTypeRegistry.APPLICATION_JSON);
		assertEquals(ResponseCode.VALID, response.getCode());
		response = client.post(msg3, MediaTypeRegistry.APPLICATION_JSON);
		assertEquals(ResponseCode.VALID, response.getCode());

		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1111);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			response = client.post(String.format(msg4, i), MediaTypeRegistry.APPLICATION_JSON);
			assertEquals(ResponseCode.VALID, response.getCode());
		}

		// Send (fake) status messages
		for (int i = 1; i <= 100; i++) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int targetVoltage = 350 + (int) Math.floor(Math.random() * 50);
			int targetCurrent = 80 + (int) Math.floor(Math.random() * 20);
			response = client.post(String.format(status, targetVoltage, targetCurrent, i, targetVoltage, targetCurrent, (i == 100 ? "true" : "false")), MediaTypeRegistry.APPLICATION_JSON);
			assertEquals(ResponseCode.VALID, response.getCode());
		}
	}

}
