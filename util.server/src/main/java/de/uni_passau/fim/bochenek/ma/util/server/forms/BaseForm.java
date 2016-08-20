package de.uni_passau.fim.bochenek.ma.util.server.forms;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import ch.ethz.inf.vs.hypermedia.corehal.model.CoREHalBase;
import de.uni_passau.fim.bochenek.ma.util.server.enums.ChargingType;

public class BaseForm extends CoREHalBase {

	private static Logger logger = Logger.getLogger(BaseForm.class.getName());

	/**
	 * 
	 * @param jo
	 * @return
	 */
	public boolean parse(JsonObject jo) {
		for (Field field : this.getAllDeclaredFields()) {
			field.setAccessible(true);
			if (jo.has(field.getName())) {
				try {

					// TODO Forget about this hacky stuff and come up with something better!
					switch (field.getGenericType().getTypeName()) {
						case "int" :
							field.set(this, jo.get(field.getName()).getAsInt());
							break;
						case "double" :
							field.set(this, jo.get(field.getName()).getAsDouble());
							break;
						case "de.uni_passau.fim.bochenek.ma.util.server.enums.ChargingType" :
							field.set(this, ChargingType.valueOf(jo.get(field.getName()).getAsString()));
							break;
						default :
							logger.log(Level.SEVERE, "Well, you didn't think about: " + field.getGenericType().getTypeName());
							break;
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return
	 */
	protected LinkedList<Field> getAllDeclaredFields() {
		LinkedList<Field> fields = new LinkedList<Field>();
		fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));

		// Get declared fields of superclass
		if (this.getClass().getSuperclass() != null) {
			fields.addAll(Arrays.asList(this.getClass().getSuperclass().getDeclaredFields()));
		}

		return fields;
	}

}
