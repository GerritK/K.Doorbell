package net.gerritk.homesystem.plugins;

import net.gerritk.homesystem.exceptions.InvalidDescriptionException;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;

public class PluginDescriptionFile {
	private String name;
	private String main;
	private String version;

	public PluginDescriptionFile(final InputStream stream) throws IOException, ParseException, InvalidDescriptionException {
		JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		Object parse = parser.parse(stream);

		if(parse == null) {
			throw new IllegalArgumentException("invalid plugin description file");
		}
		load(parse);
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return name + " v" + version;
	}

	public String getMain() {
		return main;
	}

	public String getVersion() {
		return version;
	}

	private void load(Object parse) throws InvalidDescriptionException {
		if(!(parse instanceof JSONObject)) {
			throw new IllegalArgumentException("parse must be instance of json object!");
		}

		JSONObject value = (JSONObject) parse;
		name = String.valueOf(value.get("name"));
		main = String.valueOf(value.get("main"));
		version = String.valueOf(value.get("version"));

		if(name == null || name.trim().isEmpty()) {
			throw new InvalidDescriptionException("plugin name must not be null or empty!");
		}
		if(main == null || main.trim().isEmpty()) {
			throw new InvalidDescriptionException("plugin main of '" + name + "' must not be null or empty!");
		}
		if(version == null || version.trim().isEmpty()) {
			throw new InvalidDescriptionException("plugin version of '" + name + "' must not be null or empty!");
		}
	}
}
