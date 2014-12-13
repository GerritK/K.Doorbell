package net.gerritk.doorbell.interfaces;

import net.minidev.json.JSONObject;

public interface JSONSerializable {
	public JSONObject toJSON();

	public String toJSONString();
}
