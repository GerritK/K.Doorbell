package net.gerritk.doorbell.interfaces;

import net.minidev.json.JSONObject;

public interface JSONConvertable {
	public JSONObject toJSON();

	public String toJSONString();
}
