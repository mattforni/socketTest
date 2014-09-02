package com.example.sockettest;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonSongSerializer implements JsonSerializer<Map<String,String>> {

	@Override
	public JsonElement serialize(Map<String,String> song, Type arg1, JsonSerializationContext arg2) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("OWNER_ID", song.get("ownerId"));
        jsonObject.addProperty("TITLE", song.get("title"));
        jsonObject.addProperty("ARTIST", song.get("artist"));
        jsonObject.addProperty("PATH", song.get("path"));
		
        return jsonObject;
	}

}
