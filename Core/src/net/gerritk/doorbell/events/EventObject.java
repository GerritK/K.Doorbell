package net.gerritk.doorbell.events;

import net.gerritk.doorbell.annotations.ConvertToJSON;
import net.gerritk.doorbell.interfaces.JSONConvertable;
import net.minidev.json.JSONObject;

import java.lang.reflect.Field;

public class EventObject implements JSONConvertable {
	protected Throwable throwable;

	public EventObject(Throwable throwable) {
		this.throwable = throwable;
	}

	public EventObject() {
		this(null);
	}

	@Override
	public JSONObject toJSON() {
		Field[] fields = getClass().getFields();
		JSONObject result = new JSONObject();

		for(Field field : fields) {
			if(field.isAnnotationPresent(ConvertToJSON.class)) {
				try {
					result.put(field.getName(), field.get(this));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					if(throwable != null) {
						throwable = e.getCause();
						break;
					}
				}
			}
		}

		JSONObject event = new JSONObject();
		event.put("result", result);
		if(throwable != null) {
			event.put("error", throwable);
		}

		return event;
	}

	@Override
	public String toJSONString() {
		return toJSON().toJSONString();
	}
}
