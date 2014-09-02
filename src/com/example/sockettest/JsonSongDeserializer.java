package com.example.sockettest;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class JsonSongDeserializer implements JsonDeserializer<Map<String,String>> {

	@Override
	public Map<String,String> deserialize(JsonElement serializedId, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		Map<String,String> song = new HashMap<String,String>();
		final JsonObject jsonObject = serializedId.getAsJsonObject();
		
		song.put("title",jsonObject.get("TITLE").getAsString());
		song.put("artist",jsonObject.get("ARTIST").getAsString());
		song.put("path",jsonObject.get("PATH").getAsString());
		song.put("ownerId",jsonObject.get("OWNER_ID").getAsString());
		
		return song;
	}
}

